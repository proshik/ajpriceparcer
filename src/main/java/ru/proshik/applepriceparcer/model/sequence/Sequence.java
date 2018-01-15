package ru.proshik.applepriceparcer.model.sequence;

import ru.proshik.applepriceparcer.model.OperationType;

public interface Sequence<T> {

    OperationType getOperationType();

    T getData();

}
