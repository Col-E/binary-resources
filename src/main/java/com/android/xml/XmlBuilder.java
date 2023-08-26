/*
 * Copyright (C) 2016 The Android Open Source Project
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
package com.android.xml;

import javax.annotation.Nonnull;

/**
 * Builds XML strings. Arguments are not validated or escaped. This class is designed to replace
 * hand writing XML snippets in string literals.
 */
public final class XmlBuilder {
    public static final String ATTR_LAYOUT_HEIGHT = "layout_height";
    public static final String ATTR_LAYOUT_WIDTH = "layout_width";
    public static final String PREFERENCE_CATEGORY = "PreferenceCategory";
    public static final String VALUE_MATCH_PARENT = "match_parent";
    public static final String VALUE_WRAP_CONTENT = "wrap_content";

    private enum Construct {
        NULL,
        START_TAG,
        ATTRIBUTE,
        CHARACTER_DATA,
        END_TAG
    }

    private final StringBuilder stringBuilder = new StringBuilder();

    private Construct lastAppendedConstruct = Construct.NULL;
    private int indentationLevel;

    @Nonnull
    public XmlBuilder startTag(@Nonnull String name) {
        if (!lastAppendedConstruct.equals(Construct.END_TAG)) {
            int length = stringBuilder.length();
            if (length > 0) {
                stringBuilder.replace(length - 1, length, ">\n");
            }
        }

        if (indentationLevel != 0) {
            stringBuilder.append('\n');
        }

        indent();

        stringBuilder.append('<').append(name).append('\n');

        indentationLevel++;
        lastAppendedConstruct = Construct.START_TAG;

        return this;
    }

    @Nonnull
    public XmlBuilder androidAttribute(@Nonnull String name, boolean value) {
        return androidAttribute(name, Boolean.toString(value));
    }

    @Nonnull
    public XmlBuilder androidAttribute(@Nonnull String name, int value) {
        return androidAttribute(name, Integer.toString(value));
    }

    @Nonnull
    public XmlBuilder androidAttribute(@Nonnull String name, @Nonnull String value) {
        return attribute("android", name, value);
    }

    @Nonnull
    public XmlBuilder attribute(@Nonnull String name, @Nonnull String value) {
        return attribute("", name, value);
    }

    @Nonnull
    public XmlBuilder attribute(
            @Nonnull String namespacePrefix, @Nonnull String name, @Nonnull String value) {
        indent();

        if (!namespacePrefix.isEmpty()) {
            stringBuilder.append(namespacePrefix).append(':');
        }

        stringBuilder.append(name).append("=\"").append(value).append("\"\n");

        lastAppendedConstruct = Construct.ATTRIBUTE;
        return this;
    }

    @Nonnull
    public XmlBuilder wrapContent() {
        return withSize(VALUE_WRAP_CONTENT, VALUE_WRAP_CONTENT);
    }

    @Nonnull
    public XmlBuilder matchParent() {
        return withSize(VALUE_MATCH_PARENT, VALUE_MATCH_PARENT);
    }

    @Nonnull
    public XmlBuilder withSize(@Nonnull String width, @Nonnull String height) {
        androidAttribute(ATTR_LAYOUT_WIDTH, width);
        androidAttribute(ATTR_LAYOUT_HEIGHT, height);
        return this;
    }

    @Nonnull
    public XmlBuilder characterData(@Nonnull String data) {
        if (lastAppendedConstruct.equals(Construct.START_TAG)
                || lastAppendedConstruct.equals(Construct.ATTRIBUTE)) {
            int length = stringBuilder.length();
            stringBuilder.replace(length - 1, length, ">\n");
        }

        indent();

        stringBuilder.append(data).append('\n');

        lastAppendedConstruct = Construct.CHARACTER_DATA;
        return this;
    }

    @Nonnull
    public XmlBuilder endTag(@Nonnull String name) {
        return endTagImpl(name, !name.endsWith("Layout") && !name.equals(PREFERENCE_CATEGORY));
    }

    @Nonnull
    public XmlBuilder seperateEndTag(@Nonnull String name) {
        return endTagImpl(name, false);
    }

    @Nonnull
    private XmlBuilder endTagImpl(@Nonnull String name, boolean useEmptyElementTag) {
        if (lastAppendedConstruct.equals(Construct.START_TAG)
                || lastAppendedConstruct.equals(Construct.ATTRIBUTE)) {
            int length = stringBuilder.length();

            if (useEmptyElementTag) {
                stringBuilder.deleteCharAt(length - 1);
            } else {
                stringBuilder.replace(length - 1, length, ">\n\n");
            }
        }

        indentationLevel--;

        if ((lastAppendedConstruct.equals(Construct.START_TAG)
                        || lastAppendedConstruct.equals(Construct.ATTRIBUTE))
                && useEmptyElementTag) {
            stringBuilder.append(" />\n");
        } else {
            indent();

            stringBuilder.append("</").append(name).append(">\n");
        }

        lastAppendedConstruct = Construct.END_TAG;
        return this;
    }

    private void indent() {
        for (int i = 0; i < indentationLevel; i++) {
            stringBuilder.append("    ");
        }
    }

    @Nonnull
    @Override
    public String toString() {
        return stringBuilder.toString();
    }
}
