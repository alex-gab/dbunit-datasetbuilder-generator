package org.dbunit.dataset.builder.processors;

import javax.lang.model.element.Element;

public final class ProcessingException extends Exception {
    private final Element element;

    public ProcessingException(Element element, String msg, Object... args) {
        super(String.format(msg, args));
        this.element = element;
    }

    public final Element getElement() {
        return element;
    }
}
