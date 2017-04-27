package controller;

import java.io.IOException;

/**
 * @author Ritwik Banerjee
 * @author Zhi Zou
 */
public interface FileController {

    void handleCreateNewProfile();
    void handleLogin();
    void handleWordType(int style);
    void handleHome();
    void handleStart();
    void handleLogout();
    void handleCheckProfile();

    void handlePauseResume();

    void handleHelp();
}
