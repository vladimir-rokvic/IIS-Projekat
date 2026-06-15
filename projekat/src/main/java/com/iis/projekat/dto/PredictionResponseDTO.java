package com.iis.projekat.dto;

import java.util.List;

public class PredictionResponseDTO {
    private List<PredictionDTO> predictions;

    public List<PredictionDTO> getPredictions() {
        return predictions;
    }

    public void setPredictions(List<PredictionDTO> predictions) {
        this.predictions = predictions;
    }
}
