package ru.bmstu.owncraft.healthobserver.data;

import java.io.Serializable;

public class Pulse implements Serializable {
    public long startTime;
    public long endTime;

    public float average_bpm;
    public float max_bpm;
    public float min_bpm;
}
