/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.build.docs.dsl.docbook

import org.gradle.build.docs.dsl.model.ClassMetaData
import org.gradle.build.docs.dsl.model.PropertyMetaData
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node

class ClassDoc {
    final Element classSection
    final String className
    final String id
    final String classSimpleName
    final ClassMetaData classMetaData
    private final JavadocConverter javadocConverter
    private final DslDocModel model

    ClassDoc(String className, Element classContent, Document targetDocument, ClassMetaData classMetaData, ExtensionMetaData extensionMetaData, DslDocModel model, JavadocConverter javadocConverter) {
        this.className = className
        id = className
        classSimpleName = className.tokenize('.').last()
        this.classMetaData = classMetaData
        this.javadocConverter = javadocConverter
        this.model = model

        classSection = targetDocument.createElement('chapter')

        classSection.setAttribute('id', id)
        classSection.addFirst {
            title(classSimpleName)
        }
        classContent.childNodes.each { Node n ->
            classSection << n
        }
    }

    ClassDoc mergeContent() {
        mergeDescription()
        mergeProperties()
        mergeMethods()
        return this
    }

    ClassDoc mergeDescription() {
        def properties = getSection('Properties')

        def javadocComment = javadocConverter.parse(classMetaData)
        javadocComment.docbook.each { node ->
            properties.addBefore(node)
        }

        properties.addBefore {
            section {
                title('API Documentation')
                para {
                    apilink('class': className, style: style)
                }
            }
        }

        return this
    }

    ClassDoc mergeProperties() {
        propertiesTable.addFirst { title("Properties - $classSimpleName") }
        def propertyTableHeader = propertiesTable.thead[0].tr[0]
        propertyTableHeader.td[0].addAfter { td('Description'); td('Type') }

        Set<String> props = [] as Set

        propertiesTable.tr.each { Element tr ->
            def cells = tr.td
            if (cells.size() < 1) {
                throw new RuntimeException("Expected at least 1 cell in <tr>, found: $tr")
            }
            String propName = cells[0].text().trim()
            props << propName
            PropertyMetaData property = classMetaData.findProperty(propName)
            if (!property) {
                throw new RuntimeException("No metadata for property '$className.$propName'. Available properties: ${classMetaData.propertyNames}")
            }
            String type = property.type
            tr.td[0].children = { literal(propName) }
            tr.td[0].addAfter { td() }
            javadocConverter.parse(property).docbook.each { node ->
                tr.td[1] << node
            }
            tr.td[1].addAfter {
                td {
                    if (type.startsWith('org.gradle')) {
                        apilink('class': type)
                    } else if (type.startsWith('java.lang.') || type.startsWith('java.util.') || type.startsWith('java.io.')) {
                        classname(type.tokenize('.').last())
                    } else {
                        classname(type)
                    }
                    if (!property.writeable) {
                        text(" (read-only)")
                    }
                }
            }
        }

        if (classMetaData.superClassName) {
            ClassDoc supertype = model.getClassDoc(classMetaData.superClassName)
            supertype.propertiesTable.tr.each { Element tr ->
                String propName = tr.td[0].text().trim()
                if (props.add(propName)) {
                    while (tr.td.size() < propertyTableHeader.td.size()) {
                        tr << { td() }
                    }
                    propertiesTable << tr
                }
            }
        }

        return this
    }

    ClassDoc mergeMethods() {
        methodsTable.addFirst { title("Methods - $classSimpleName")}

        if (classMetaData.superClassName) {
            ClassDoc supertype = model.getClassDoc(classMetaData.superClassName)
            supertype.methodsTable.tr.each { Element tr ->
                methodsTable << tr
            }
        }

        return this
    }

    Element getPropertiesTable() {
        return getSection('Properties').table[0]
    }

    Element getMethodsTable() {
        return getSection('Methods').table[0]
    }

    String getStyle() {
        return classMetaData.groovy ? 'groovydoc' : 'javadoc'
    }

    private Element getSection(String title) {
        def sections = classSection.section.findAll { it.title[0].text().trim() == title }
        if (sections.size() < 1) {
            throw new RuntimeException("Docbook content for $className does not contain a '$title' section.")
        }
        return sections[0]
    }

    Element getHasDescription() {
        def paras = classSection.para
        return paras.size() > 0 ? paras[0] : null
    }

    Element getDescription() {
        def paras = classSection.para
        if (paras.size() < 1) {
            throw new RuntimeException("Docbook content for $className does not contain a description paragraph.")
        }
        return paras[0]
    }
}