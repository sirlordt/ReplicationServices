package ReplicationCommonClasses;

import java.util.LinkedHashMap;

public class CReplicationCommandBlock {
	
	public String strInstanceID;
	public String strSourceDataBlockID;
	public String strTransactionID;
	public String strDataBlockID; //DataBlockID
	public String strCommandID;
	public String strCommand;
	public String strUser;
	public String strDatabase;
	
	public LinkedHashMap<String,String> Params;
	
	public CReplicationCommandBlock() {
		
		strInstanceID = "";
		strSourceDataBlockID = "";
		strTransactionID = "";
		strDataBlockID = "";
		strCommandID = "";
		strCommand = "";
		strUser = "";
		strDatabase = "";
		
		Params = new LinkedHashMap<String,String>();
		
	}

	public CReplicationCommandBlock( CReplicationCommandBlock ReplicationCommandClone ) {
		
		strInstanceID = ReplicationCommandClone.strInstanceID;
		strSourceDataBlockID = ReplicationCommandClone.strSourceDataBlockID;
		strTransactionID = ReplicationCommandClone.strTransactionID;
		strDataBlockID = ReplicationCommandClone.strDataBlockID;
		strCommandID = ReplicationCommandClone.strCommandID;
		strCommand = ReplicationCommandClone.strCommand;
		strUser = ReplicationCommandClone.strUser;
		strDatabase = ReplicationCommandClone.strDatabase;
		
		Params = new LinkedHashMap<String,String>( ReplicationCommandClone.Params );
		
	}
	
}
