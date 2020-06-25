package com.accenture.hr.model;

import com.accenture.hr.enums.WorkSpaceStatus;
import com.accenture.hr.service.ImageService;

public class WorkSpace {

    private ImageService imageService;

    private final int x;
    private final int y;
    private WorkSpaceStatus status;

    public WorkSpace(int x, int y, ImageService imageService) {
        this.x = x;
        this.y = y;
        this.imageService = imageService;
        this.status = setStatus(WorkSpaceStatus.FREE);
    }

    public WorkSpaceStatus setStatus(WorkSpaceStatus status) {
        this.status = status;
        imageService.drawWorkSpace(x, y, status.getColor());
        return status;
    }
}