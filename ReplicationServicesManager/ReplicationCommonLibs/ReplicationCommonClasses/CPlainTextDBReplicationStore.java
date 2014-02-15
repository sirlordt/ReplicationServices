package ReplicationCommonClasses;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.UUID;

import CommonClasses.CExpresionsFilters;
import CommonClasses.CLanguage;
import CommonClasses.ConstantsCommonClasses;
import ExtendedLogger.CExtendedLogger;

public class CPlainTextDBReplicationStore implements IDBReplicationStore {

	protected static final String _XML_Header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
	protected static final String _Header = "<Header ";

	public static final String _ReplicationStorageFilesIndex = "ReplicatorStorageFilesIndex.txt";
	
	protected CConfigPlainTextDBReplicationStore ConfigPlainTextDBReplicationStore;
	
	protected LinkedList<CPlainTextDBReplicationStoreHelper> PlainTextDBReplicationStoreHelpers;
	
	protected LinkedList<String> ReplicationStoreFilesIndex; 
	
	@Override
	public boolean loadConfig( String strRunningPath, String strStoreDir, String strName, String strTargeDatabase, String strConfigFile, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;
		
		ConfigPlainTextDBReplicationStore = CConfigPlainTextDBReplicationStore.getConfigPlainTextDBReplicationStore( strRunningPath );
		
		if ( ConfigPlainTextDBReplicationStore.loadConfig( strConfigFile, Logger, Lang ) ) {
			
			ConfigPlainTextDBReplicationStore.strName = strName;

			ConfigPlainTextDBReplicationStore.strTargetDatabase = strTargeDatabase;
			
			ConfigPlainTextDBReplicationStore.strConfigFile = strConfigFile;
			
			ConfigPlainTextDBReplicationStore.strStoreDir = strStoreDir;
			
			PlainTextDBReplicationStoreHelpers = new LinkedList<CPlainTextDBReplicationStoreHelper>();
			
			ReplicationStoreFilesIndex = this.loadReplicationStorageFilesIndex( strStoreDir + File.separatorChar + strName + File.separatorChar + _ReplicationStorageFilesIndex, Logger, Lang );
			
			checkWorkingStoreFiles( strStoreDir + File.separatorChar + strName + File.separatorChar, Logger, Lang );
			
			bResult = true;
			
		}
		
		return bResult;
		
	}

	@Override
	public String getName() {
	
		return ConfigPlainTextDBReplicationStore.strName;
		
	}

	@Override
	public String getTargeDatabase() {
		
		return ConfigPlainTextDBReplicationStore.strTargetDatabase;
		
	}

	@Override
	public String getConfigFile() {
		
		return ConfigPlainTextDBReplicationStore.strConfigFile;
		
	}
	

	public void checkWorkingStoreFiles( String strStoreDir, CExtendedLogger Logger, CLanguage Lang ) {
	
		try {
			
			File[] workingStoreFileList = net.maindataservices.Utilities.recursiveFindFilesToLoad( strStoreDir, ".working", 1, 1);

			SimpleDateFormat DF = new SimpleDateFormat( "dd/MM/yyy" ); 
			SimpleDateFormat TF = new SimpleDateFormat( "HH:mm:ss:SSS" ); 
			
			String strUUID = UUID.randomUUID().toString();
			
			String strMinimalBlockData = _XML_Header;
			strMinimalBlockData += _Header + "CreatedDate=\"" + DF.format( new Date() ) + "\" CreatedTime=\"" + TF.format( new Date() ) + "\" InstanceID=\"" + strUUID + "\" DataBlockID=\"" + strUUID + "\" />\n"; 
			strMinimalBlockData += "<DataBlock>\n";
			strMinimalBlockData += "</DataBlock>\n";
			
			long lngMinimalDataLength = strMinimalBlockData.length() + 15;
			
			for ( File workingStoreFile : workingStoreFileList ) {

				if ( workingStoreFile.length() >= lngMinimalDataLength ) {
				
					RandomAccessFile Reader = new RandomAccessFile( workingStoreFile, "rw" );
					String strLastLine = "";
					String strLine;

					while ( ( strLine = Reader.readLine() ) != null ) {

						strLastLine = strLine;

					}

					if ( strLastLine.equalsIgnoreCase( "</DataBlock>" ) == false ) {

						Reader.writeChars( "</DataBlock>\n" );

					}

					Reader.close();			

					String strFileName = workingStoreFile.getName();
					
					if ( strFileName.endsWith( ".working" ) ) {
						
						strFileName = strFileName.substring( 0, strFileName.length() - ".working".length() );
						
					}
					
					//Publish the current store file changing the name from ????????.working to ????????.db
					workingStoreFile.renameTo( new File( ConfigPlainTextDBReplicationStore.strStoreDir + ConfigPlainTextDBReplicationStore.strName + File.separatorChar + strFileName + ".db" ) );
					
					publishDataBlockToStoreIndex( strFileName + ".db", Logger, Lang);
					
				}
				else {
					
					workingStoreFile.delete();
					
				}
				
			}
		
		}
    	catch ( Error Err ) {

    		if ( Logger != null )
    			Logger.logError( "-1020", Err.getMessage(), Err );

    	}
    	catch ( Exception Ex ) {

    		if ( Logger != null )
    			Logger.logException( "-1021", Ex.getMessage(), Ex );

    	}
	    
	}
	
	
	@Override
	public LinkedList<String> buildReplicationStoreFilesIndex( String strStoreDir, CExtendedLogger Logger, CLanguage Lang ) {

		LinkedList<String> Result = new LinkedList<String>();
		
		try {

			File[] DBStoreFileList = net.maindataservices.Utilities.recursiveFindFilesToLoad( strStoreDir, ".db", 1, 1);

			LinkedHashMap<String, Long> DBStoreCreationFilesList = new LinkedHashMap<String, Long>(); 
			SimpleDateFormat DTF = new SimpleDateFormat( "dd/MM/yyy HH:mm:ss:SSS" ); 

			for ( File DBStoreFile: DBStoreFileList ) {

				RandomAccessFile Reader = new RandomAccessFile( DBStoreFile, "r" );
				String strLine;

				while ( ( strLine = Reader.readLine() ) != null ) {

					if ( strLine.toLowerCase().startsWith( _Header.toLowerCase() ) ) {
						
						String strCreatedDate = "";
						String strCreatedTime = "";
						
						strLine = strLine.substring( _Header.length() );
						strLine = strLine.substring( 0, strLine.length() - 3 ).trim();
						
						String[] strTokens = strLine.split( " +(?=(?:([^\"]*\"){2})*[^\"]*$)" );
						
						for ( int intIndex = 0; intIndex < strTokens.length; intIndex++ ) {
							
							String strToken = strTokens[ intIndex ];
							
							if ( strToken.toLowerCase().startsWith( "createddate" ) ) {
								
								String[] strKeyValue = strToken.split( "=\"" );
								
								if ( strKeyValue.length > 1 )
									strCreatedDate = strKeyValue[ 1 ].substring( 0,  strKeyValue[ 1 ].length() - 1 );
								
							}
							else if ( strToken.toLowerCase().startsWith( "createdtime" ) ) {
								
								String[] strKeyValue = strToken.split( "=\"" );
								
								if ( strKeyValue.length > 1 )
									strCreatedTime = strKeyValue[ 1 ].substring( 0,  strKeyValue[ 1 ].length() - 1 );
								
							}
							
						}
						
						try {
						
							DBStoreCreationFilesList.put( DBStoreFile.getName(), DTF.parse( strCreatedDate + " " + strCreatedTime ).getTime() );
							
						}
						catch ( Exception Ex ) {
							
							if ( Logger != null )
								Logger.logException( "-1020", Ex.getMessage(), Ex );
							
						}
						
					}

				}

				Reader.close();

			}
			
			DBStoreCreationFilesList = (LinkedHashMap<String, Long>) net.maindataservices.Utilities.sortMapByValues( DBStoreCreationFilesList );
			
			for ( Entry<String,Long> DBStoreCreationFile: DBStoreCreationFilesList.entrySet() ) {
				
				Result.add( DBStoreCreationFile.getKey() );
				
			}

		}
		catch ( Error Err ) {

			if ( Logger != null )
				Logger.logError( "-1025", Err.getMessage(), Err );

		}
		catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.logException( "-1026", Ex.getMessage(), Ex );

		}
		
		return Result;
		
	}

	protected LinkedList<String> loadReplicationStorageFilesIndex( String strFilePath, CExtendedLogger Logger, CLanguage Lang ) {
		
		LinkedList<String> Result = null;
		
		try {
		
			File ReplicationStorageFilesIndex = new File( strFilePath );

			if ( ReplicationStorageFilesIndex.exists() && ReplicationStorageFilesIndex.canRead() ) {

				Result = new LinkedList<String>();
				
				BufferedReader reader = new BufferedReader(new FileReader( strFilePath ) );
				String strLine;

				while ( ( strLine = reader.readLine() ) != null ) {

					Result.add( strLine );

				}

				reader.close();			

			}
			else {
				
				Result = buildReplicationStoreFilesIndex( ReplicationStorageFilesIndex.getParent(), Logger, Lang );
				
				if ( Result.size() > 0 )
					saveReplicationStorageFilesIndex( Result, strFilePath, Logger, Lang );
				
			}
			
		}
    	catch ( Error Err ) {

    		if ( Logger != null )
    			Logger.logError( "-1020", Err.getMessage(), Err );

    	}
    	catch ( Exception Ex ) {

    		if ( Logger != null )
    			Logger.logException( "-1021", Ex.getMessage(), Ex );

    	}
		
		return Result;
		
	}
	
	protected synchronized void saveReplicationStorageFilesIndex( LinkedList<String> ReplicationStoreFilesIndex, String strFilePath, CExtendedLogger Logger, CLanguage Lang ) {
		
		try {
			
			File ReplicationStorageFilesIndex = new File( strFilePath );

			ReplicationStorageFilesIndex.delete();
			
			if ( ReplicationStorageFilesIndex.exists() == false ) {

				BufferedWriter writer = new BufferedWriter( new FileWriter( strFilePath ) );

				for ( String strLine: ReplicationStoreFilesIndex ) {

					writer.write( strLine + "\n" );

				}

				writer.flush();
				writer.close();			

			}
		
		}
    	catch ( Error Err ) {

    		if ( Logger != null )
    			Logger.logError( "-1020", Err.getMessage(), Err );

    	}
    	catch ( Exception Ex ) {

    		if ( Logger != null )
    			Logger.logException( "-1021", Ex.getMessage(), Ex );

    	}
		
	} 
	
	protected boolean checkExpressionsFilters( String strCommand, CExtendedLogger Logger ) {
		
		boolean bResult = false;
		
		if ( ConfigPlainTextDBReplicationStore.ExpFilters == null ) {
			
			bResult = true;
			
		}
		else if ( ConfigPlainTextDBReplicationStore.ExpFilters.checkExpressionInFilters( strCommand, Logger ) ) {
			
			if ( ConfigPlainTextDBReplicationStore.ExpFilters.strType.equalsIgnoreCase( ConstantsCommonClasses._Type_Allow ) )
				bResult = true;
			
		}
		else {
			
			if ( ConfigPlainTextDBReplicationStore.ExpFilters.strType.equalsIgnoreCase( ConstantsCommonClasses._Type_Block ) )
				bResult = true;
			
		}
		
		return bResult;
		
	} 
	
	protected CPlainTextDBReplicationStoreHelper getPlainTextDBReplicationStoreHelper( String strInstanceID, CExtendedLogger Logger, CLanguage Lang ) {
		
		CPlainTextDBReplicationStoreHelper Result = null;
		
		for ( CPlainTextDBReplicationStoreHelper PlainTextDBReplicationStoreHelper: PlainTextDBReplicationStoreHelpers ) {
			
			if ( PlainTextDBReplicationStoreHelper.strInstanceID.equalsIgnoreCase( strInstanceID ) ) {
				
				Result = PlainTextDBReplicationStoreHelper;
				break;
				
			}
			
		}
		
		if ( Result == null ) {
			
			Result = new CPlainTextDBReplicationStoreHelper( strInstanceID, this, Logger, Lang );
			
			PlainTextDBReplicationStoreHelpers.add( Result );
			
		}
		
		return Result;
		
	}
	
	@Override
	public boolean addReplicationCommandBlockToStore( CReplicationCommandBlock ReplicationCommandBlock, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;
		
		if ( this.ConfigPlainTextDBReplicationStore.strTargetDatabase.equalsIgnoreCase( ReplicationCommandBlock.strDatabase ) ) {
			
			try {

				if ( checkExpressionsFilters( ReplicationCommandBlock.strCommand, Logger ) ) {	

					CPlainTextDBReplicationStoreHelper PlainTextDBReplicationStoreHelper = getPlainTextDBReplicationStoreHelper( ReplicationCommandBlock.strInstanceID, Logger, Lang );

					PlainTextDBReplicationStoreHelper.addReplicationCommandBlockToStore( ReplicationCommandBlock, Logger, Lang );
					
				}
			
			}
			catch ( Error Err ) {

				if ( Logger != null )
					Logger.logError( "-1020", Err.getMessage(), Err );

			}
			catch ( Exception Ex ) {

				if ( Logger != null )
					Logger.logException( "-1021", Ex.getMessage(), Ex );

			}
			
		}
		
		return bResult;
		
	}

	@Override
	public LinkedList<String> getAllDataBlocksID( String strDataBlockID, CExtendedLogger Logger, CLanguage Lang ) {

		return null;
		
	}

	@Override
	public String getNextDataBlockID( String strDataBlockID, CExtendedLogger Logger, CLanguage Lang ) {

		return null;
		
	}

	@Override
	public LinkedList<String> getCommandBlocksIDInDataBlockID( String strDataBlockID, CExtendedLogger Logger, CLanguage Lang ) {
		
		return null;
		
	}

	@Override
	public LinkedList<CReplicationCommandBlock> getCommandBlocksInDataBlockID( String strDataBlockID, CExtendedLogger Logger, CLanguage Lang ) {

		return null;
		
	}

	@Override
	public CReplicationCommandBlock getCommandBlock( String strDataBlockID, String strCommandBlockID, CExtendedLogger Logger, CLanguage Lang ) {
		
		return null;
		
	}

	@Override
	public CReplicationCommandBlock getNextCommandBlock( String strDataBlockID, String strCommandBlockID, CExtendedLogger Logger, CLanguage Lang ) {

		return null;
		
	}

	@Override
	public InputStream getStreamDataBlock( String strDataBlockID, CExtendedLogger Logger, CLanguage Lang ) {

		return null;
	
	}

	@Override
	public IDBReplicationSecurity getDBReplicationSecurity() {

		return null;
		
	}

	@Override
	public void setDBReplicationSecurity( IDBReplicationSecurity DBReplicationSecurity ) {

		
		
	}

	@Override
	public IDBReplicationWorker getPullDBReplicationWorker() {

		return null;
		
	}

	@Override
	public void setPullReplicationWorker( IDBReplicationWorker PullDBReplicationWorker) {
		
		
		
	}

	@Override
	public IDBReplicationWorker getPushDBReplicationWorker() {

		return null;
		
	}

	@Override
	public void setPushReplicationWorker( IDBReplicationWorker PushDBReplicationWorker ) {

		
		
	}

	@Override
	public CExpresionsFilters getFilters() {

		return ConfigPlainTextDBReplicationStore.ExpFilters;
		
	}

	@Override
	public void setFilters( CExpresionsFilters ExpFilters ) {
		
		ConfigPlainTextDBReplicationStore.ExpFilters = ExpFilters;
		
	}

	@Override
	public void publishDataBlockToStoreIndex( String strDataBlockFileName, CExtendedLogger Logger, CLanguage Lang ) {

		if ( ReplicationStoreFilesIndex.contains( strDataBlockFileName ) == false ) {
			
			synchronized (ReplicationStoreFilesIndex) {

				ReplicationStoreFilesIndex.add( strDataBlockFileName );
				
			}
			
			saveReplicationStorageFilesIndex( ReplicationStoreFilesIndex, ConfigPlainTextDBReplicationStore.strStoreDir + File.separatorChar + ConfigPlainTextDBReplicationStore.strName + File.separatorChar + _ReplicationStorageFilesIndex, Logger, Lang );			
			
		}
		
	}


}
