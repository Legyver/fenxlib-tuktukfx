package com.legyver.fenxlib.tuktukfx.bindings;

import com.legyver.fenxlib.locator.query.bindings.AbstractBindingFactory;
import com.legyver.fenxlib.tuktukfx.config.TukTukFxApplicationOptions;
import com.legyver.fenxlib.tuktukfx.task.adapter.AbortableTaskStatusAdapter;
import javafx.beans.property.BooleanProperty;

public class TaskBindingFactory extends AbstractBindingFactory {
	private final BooleanProperty shuttingDown;

	public TaskBindingFactory(TukTukFxApplicationOptions applicationOptions) {
		this.shuttingDown = applicationOptions.shuttingDownProperty();
	}

	public void taskAbortObservesShutdown(AbortableTaskStatusAdapter abortable) {
		shuttingDown.addListener((obsersvableValue, oldValue, newValue) -> {
			if (newValue) {
				abortable.setAborted(true);
			}
		});
	}

}
