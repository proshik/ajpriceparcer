package ru.proshik.applepriceparcer.model.sequence;

import ru.proshik.applepriceparcer.model.OperationType;
import ru.proshik.applepriceparcer.model.StepType;

public class DataSequence implements Sequence<SelectShopSequence> {

    private OperationType operationType;
    private StepType stepType;
    private SelectShopSequence data = new SelectShopSequence();

    public DataSequence() {
    }

    public DataSequence(OperationType operationType, StepType stepType) {
        this.operationType = operationType;
        this.stepType = stepType;
    }

    public DataSequence(OperationType operationType, StepType stepType, SelectShopSequence data) {
        this.operationType = operationType;
        this.stepType = stepType;
        this.data = data;
    }

    public void setStepType(StepType stepType) {
        this.stepType = stepType;
    }

    public void setData(SelectShopSequence data) {
        this.data = data;
    }

    public StepType getStepType() {
        return stepType;
    }

    @Override
    public OperationType getOperationType() {
        return operationType;
    }

    @Override
    public SelectShopSequence getData() {
        return data;
    }

}
