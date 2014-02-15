package ReplicationCommonClasses;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import CommonClasses.CAbstractConfigLoader;
import CommonClasses.CExpresionsFilters;
import CommonClasses.CLanguage;
import ExtendedLogger.CExtendedLogger;

public class CConfigPlainTextDBReplicationStore extends CAbstractConfigLoader {

	protected static CConfigPlainTextDBReplicationStore ConfigPlainTextDBReplicationStore;
	
	public CExpresionsFilters ExpFilters;
	
	public String strName;
	public String strTargetDatabase;
	public String strConfigFile;
	public String strStoreDir;

	public long lngMaxSizeDataBlock;
	public long lngMaxWaitToPublishDataBlock;
	
	public static CConfigPlainTextDBReplicationStore getConfigPlainTextDBReplicationStore( String strRunningPath ) {
		
		if ( ConfigPlainTextDBReplicationStore == null ) {
			
			ConfigPlainTextDBReplicationStore = new CConfigPlainTextDBReplicationStore( strRunningPath );
			
		}
		
		return ConfigPlainTextDBReplicationStore;
		
	}
	
	public CConfigPlainTextDBReplicationStore( String strRunningPath ) {
		
		super( strRunningPath );
		
		lngMaxSizeDataBlock = ConstantsReplication._Min_Size_Data_Block;

		lngMaxWaitToPublishDataBlock = ConstantsReplication._Min_Wait_To_Publish_Block;
		
	}
	
    public boolean loadConfigSectionSystem( Node ConfigSectionNode, CLanguage Lang, CExtendedLogger Logger ) {

        boolean bResult = true;
		
        try {
		   
        	if ( ConfigSectionNode.hasAttributes() == true ) {

        		String strAttributesOrder[] = { ConstantsReplicationConfigXMLTags._Max_Size_Data_Block, ConstantsReplicationConfigXMLTags._Max_Wait_To_Publish_Block };

        		NamedNodeMap NodeAttributes = ConfigSectionNode.getAttributes();

        		for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {

        			Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );

        			if ( NodeAttribute != null ) {

        				Logger.logMessage( "1", Lang.translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
        				Logger.logMessage( "1", Lang.translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );

        				if ( NodeAttribute.getNodeName().equals( ConstantsReplicationConfigXMLTags._Max_Size_Data_Block ) ) {
        				
					        if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

					        	long lngTmpMaxSizeDataBlock = net.maindataservices.Utilities.strToLong( NodeAttribute.getNodeValue().trim() );
					        	
					        	if ( lngTmpMaxSizeDataBlock >= ConstantsReplication._Min_Size_Data_Block && lngTmpMaxSizeDataBlock <= Long.MAX_VALUE ) {
					        		
					        		lngMaxSizeDataBlock = lngTmpMaxSizeDataBlock;
					        		
					        	}
					        	else {
					        		
									Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is out of range. Must be integer value in the next inclusive range, minimum [%s] and maximum [%s]", ConstantsReplicationConfigXMLTags._Max_Size_Data_Block, Long.toString( lngTmpMaxSizeDataBlock ), Long.toString( ConstantsReplication._Min_Size_Data_Block ), Long.toString( Long.MAX_VALUE ) ) );
									Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is invalid, using the default value [%s]", ConstantsReplicationConfigXMLTags._Max_Size_Data_Block, Long.toString( lngMaxSizeDataBlock ), Long.toString( lngMaxSizeDataBlock ) ) );
									
					        	}
						        
					        }
					        else {
					        	
						        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsReplicationConfigXMLTags._Max_Size_Data_Block, Long.toString( ConstantsReplication._Min_Size_Data_Block ) ) );
					        	
					        }
        					
        				}	
        				else if ( NodeAttribute.getNodeName().equals( ConstantsReplicationConfigXMLTags._Max_Wait_To_Publish_Block ) ) {
        				
					        if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

					        	long lngTmpMaxWaitToPublishBlock = net.maindataservices.Utilities.strToLong( NodeAttribute.getNodeValue().trim() );
					        	
					        	if ( lngTmpMaxWaitToPublishBlock >= ConstantsReplication._Min_Wait_To_Publish_Block && lngTmpMaxWaitToPublishBlock <= Long.MAX_VALUE ) {
					        		
					        		lngMaxWaitToPublishDataBlock = lngTmpMaxWaitToPublishBlock;
					        		
					        	}
					        	else {
					        		
									Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is out of range. Must be integer value in the next inclusive range, minimum [%s] and maximum [%s]", ConstantsReplicationConfigXMLTags._Max_Wait_To_Publish_Block, Long.toString( lngMaxWaitToPublishDataBlock ), Long.toString( ConstantsReplication._Min_Wait_To_Publish_Block ), Long.toString( Long.MAX_VALUE ) ) );
									Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is invalid, using the default value [%s]", ConstantsReplicationConfigXMLTags._Max_Wait_To_Publish_Block, Long.toString( lngMaxWaitToPublishDataBlock ), Long.toString( lngMaxWaitToPublishDataBlock ) ) );
									
					        	}
						        
					        }
					        else {
					        	
						        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsReplicationConfigXMLTags._Max_Size_Data_Block, Long.toString( ConstantsReplication._Min_Size_Data_Block ) ) );
					        	
					        }
       					
        				}	

        			}
        			else {

        				if ( strAttributesOrder[ intAttributesIndex ].equals( ConstantsReplicationConfigXMLTags._Max_Size_Data_Block ) ) {

        					Logger.logWarning( "-1", Lang.translate( "The [%s] attribute not found, using the default value [%s]", ConstantsReplicationConfigXMLTags._Max_Size_Data_Block, Long.toString( lngMaxSizeDataBlock ) ) );

        				}
        				else if ( strAttributesOrder[ intAttributesIndex ].equals( ConstantsReplicationConfigXMLTags._Max_Wait_To_Publish_Block ) ) {
        					
        					Logger.logWarning( "-1", Lang.translate( "The [%s] attribute not found, using the default value [%s]", ConstantsReplicationConfigXMLTags._Max_Wait_To_Publish_Block, Long.toString( lngMaxWaitToPublishDataBlock ) ) );
        					
        				}

        			} 	
        		
        		}

        	}

        }
        catch ( Exception Ex ) {

        	bResult = false;

        	Logger.logException( "-1010", Ex.getMessage(), Ex );

        }

        return bResult;

    }
    
	@Override
	public boolean loadConfigSection( Node ConfigSectionNode, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = true;

		Logger.logMessage( "1", Lang.translate( "Reading XML node section: [%s]", ConfigSectionNode.getNodeName() ) );        
		
		if ( ConfigSectionNode.getNodeName().equals(  CommonClasses.ConstantsCommonConfigXMLTags._System ) == true ) {
	           
			if ( this.loadConfigSectionSystem( ConfigSectionNode, Lang, Logger ) == false ) {
				
    			Logger.logError( "-1001", Lang.translate( "Failed to load config from XML node section: [%s] ", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
				
			} 
        	 
        }
	
		return bResult;
		
	}

	@Override
	public Object sendMessage( String strMessageName, Object MessageData ) {

		return "";
		
	}

}
