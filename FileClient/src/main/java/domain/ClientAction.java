package domain;

import message.Mess;

public interface ClientAction {
    Mess action(Mess mess);
}
