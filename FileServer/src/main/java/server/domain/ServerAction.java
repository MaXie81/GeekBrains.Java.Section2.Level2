package server.domain;

import message.Mess;

public interface ServerAction {
    Mess action(Mess mess);
}