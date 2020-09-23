package mr.resourcemanagement.datatype;

public class TaskData <K,V>{	
	private K dataID;
	private V data;
	
	public TaskData(K dataID, V data){
		this.dataID 		= dataID;
		this.data   		= data;
	}

	public K getDataID() {
		return dataID;
	}

	public V getData() {
		return data;
	}

	public void setDataID(K dataID) {
		this.dataID 		= dataID;
	}

	public void setData(V data) {
		this.data 		= data;
	}
}
