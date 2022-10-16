package com.geekbrains.core.model;

import java.io.Serializable;

public abstract class AbstractMessage implements Serializable {
    abstract public CommandType getType();
}
