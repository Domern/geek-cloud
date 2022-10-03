package model;

import java.io.Serial;
import java.io.Serializable;

public interface CloudMessage extends Serializable {
    MessageType getType();
}
