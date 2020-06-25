package com.accenture.hr.enums;

import java.awt.*;

public enum WorkSpaceStatus {

    FREE(Color.GREEN),
    RESERVED(Color.YELLOW),
    OCCUPIED(Color.RED);

    private Color color;

    WorkSpaceStatus(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
