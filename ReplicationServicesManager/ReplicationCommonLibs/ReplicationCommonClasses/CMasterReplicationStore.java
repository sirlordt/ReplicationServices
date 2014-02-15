package ReplicationCommonClasses;

import java.util.LinkedList;

public class CMasterReplicationStore {

	protected static CMasterReplicationStore MasterReplicationStore = null;
	
	public static CMasterReplicationStore getMasterReplicationStore() {
		
		if ( MasterReplicationStore == null ) 
			MasterReplicationStore = new CMasterReplicationStore();
		
		return MasterReplicationStore;
		
	}
	
	protected LinkedList<IDBReplicationStore> RegisteredReplicationStore; 
	
	CMasterReplicationStore() {
		
		RegisteredReplicationStore = new LinkedList<IDBReplicationStore>();
		
	}
	
	public IDBReplicationStore getReplicationStoreByName( String strName ) {
		
		IDBReplicationStore Result = null;
		
		for ( IDBReplicationStore DBReplicationStore: RegisteredReplicationStore ) {
			
			if ( DBReplicationStore.getName().equalsIgnoreCase( strName ) ) {
				
				Result = DBReplicationStore;
				break;
				
			}
			
		}
		
		return Result;
		
	}

	public IDBReplicationStore getReplicationStoreByTargetDatabase( String strTargetDatabase ) {
		
		IDBReplicationStore Result = null;
		
		for ( IDBReplicationStore DBReplicationStore: RegisteredReplicationStore ) {
			
			if ( DBReplicationStore.getTargeDatabase().equalsIgnoreCase( strTargetDatabase ) ) {
				
				Result = DBReplicationStore;
				break;
				
			}
			
		}
		
		return Result;
		
	}
	
	public void registerReplicationStore( IDBReplicationStore DBReplicationStore ) {
		
		RegisteredReplicationStore.add( DBReplicationStore );
		
	}

	public void unregisterReplicationStore( IDBReplicationStore DBReplicationStore ) {
		
		RegisteredReplicationStore.remove( DBReplicationStore );
		
	}
	
}
