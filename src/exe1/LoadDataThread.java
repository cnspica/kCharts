package exe1;

import java.util.*;

public class LoadDataThread extends Thread {

	private List<Manager> list;

	public LoadDataThread(List<Manager> list) {
		this.list = list;
	}

	public void run() {

		for (Manager manager : list) {
            manager.loadData();
		}
	}
}
