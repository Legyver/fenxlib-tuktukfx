package com.legyver.fenxlib.tuktukfx.config;

import com.legyver.fenxlib.config.ApplicationConfigInstantiator;
import com.legyver.fenxlib.config.options.FileBasedApplicationOptions;
import com.legyver.fenxlib.tuktukfx.task.exec.TaskExecutor;
import com.legyver.fenxlib.uimodel.RecentFileAware;
import com.legyver.fenxlib.util.hook.LifecycleHook;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * ApplicationOptions that allows for exiting the app to
 * 1. Set a flag to notify any tasks that the app is shutting down (See {@link com.legyver.fenxlib.tuktukfx.bindings.TaskBindingFactory})
 * 2. Shutdown the thread-pool
 *
 */
public class TukTukFxApplicationOptions<T extends RecentFileAware> extends FileBasedApplicationOptions<T> {
	private final BooleanProperty shuttingDown = new SimpleBooleanProperty(false);

	public TukTukFxApplicationOptions(String configDirName, ApplicationConfigInstantiator instantiator, Stage primaryStage, T uiModel) throws IOException, IllegalAccessException {
		super(configDirName, instantiator, primaryStage, uiModel);
		registerHook(LifecycleHook.PRE_SHUTDOWN, () -> {
			shuttingDown.set(true);//flag for long-running running tasks to cancel

			TaskExecutor.INSTANCE.shutdownNow();//shutdown thread-pool
		});
	}

	public boolean isShuttingDown() {
		return shuttingDown.get();
	}

	public BooleanProperty shuttingDownProperty() {
		return shuttingDown;
	}

	public void setShuttingDown(boolean shuttingDown) {
		this.shuttingDown.set(shuttingDown);
	}
}
