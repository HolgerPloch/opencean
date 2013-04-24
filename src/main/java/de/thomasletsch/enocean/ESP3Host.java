package de.thomasletsch.enocean;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.thomasletsch.enocean.struct.BasicPacket;
import de.thomasletsch.enocean.struct.ByteArrayWrapper;
import de.thomasletsch.enocean.struct.UnknownPacket;

public class ESP3Host {
    private static Logger logger = Logger.getLogger("ESP3Host");

    public void sendRadio() {

    }
    
    enum ENOCEAN_MSG_STATE {
    	GET_SYNC_STATE,
    	GET_HEADER_STATE,
    	CHECK_CRC8H_STATE,
    	GET_DATA_STATE,
    	CHECK_CRC8D_STATE
    }

    public void receiveRadio(DataInputStream poInputStream) {
        logger.info("starting receiveRadio.. ");

        ByteArrayWrapper loReceiveBuffer = new ByteArrayWrapper(new byte[0]);

        ENOCEAN_MSG_STATE loState =  ENOCEAN_MSG_STATE.GET_SYNC_STATE;
        UnknownPacket loUnknownPacket = new UnknownPacket();

		try {

        while (true) {
        	
        	byte loReadbyte = (byte) poInputStream.readByte();
        	
        	loReceiveBuffer.addByte(loReadbyte);
        	
        	switch ( loState ) {
        	case GET_SYNC_STATE:
        		if ( loReadbyte == BasicPacket.SYNC_BYTE) {
        			loState = ENOCEAN_MSG_STATE.GET_HEADER_STATE; 
	                logger.info("sync byte received.. ");
        		}
        		else
        		{
	                logger.info("invalid byte received.. ");
	                loReceiveBuffer.clear();
        		}
        		break;
        		
        	case GET_HEADER_STATE:
        		if ( loReceiveBuffer.getArray().length == (BasicPacket.POS_HEADER_START + BasicPacket.HEADER_LENGTH)) {
        			loState = ENOCEAN_MSG_STATE.CHECK_CRC8H_STATE; 
	                logger.info("header received.. ");
        		}
        		else if( loReceiveBuffer.getArray().length > (BasicPacket.POS_HEADER_START + BasicPacket.HEADER_LENGTH)) 
        		{
	                logger.info("invalid header, too long. reset and start over. ");
        			loState = ENOCEAN_MSG_STATE.GET_SYNC_STATE; 
	                loReceiveBuffer.clear();
        		}
        		break;
        		
        	case CHECK_CRC8H_STATE:
        		
        		loUnknownPacket = new UnknownPacket();
        		try {
					loUnknownPacket.readHeader(loReceiveBuffer);

	                if ( loUnknownPacket.isHeaderCrc8Correct() ) {
	        			loState = ENOCEAN_MSG_STATE.GET_DATA_STATE;
		                logger.info("header crc8 ok.. ");
	                }
	                else
	                {
		                logger.info("invalid CRC8H, reset and start over. ");
	        			loState = ENOCEAN_MSG_STATE.GET_SYNC_STATE; 
		                loReceiveBuffer.clear();
	                }
        		} catch (Exception e) {
	                logger.log( Level.WARNING ,"invalid header, reset and start over. ", e);
        			loState = ENOCEAN_MSG_STATE.GET_SYNC_STATE; 
	                loReceiveBuffer.clear();
				}
        		
        		break;

        	case GET_DATA_STATE:
        		
        		if (loReceiveBuffer.getArray().length >= (BasicPacket.POS_DATA_START + 
        				loUnknownPacket.getDataLength() + loUnknownPacket.getOptionalDataLength())) {
        			loState = ENOCEAN_MSG_STATE.CHECK_CRC8D_STATE; 
	                logger.info("data ok.. ");
        		}
        		break;
        		
        	case CHECK_CRC8D_STATE:
        		loUnknownPacket = new UnknownPacket();
        		try {
	                logger.info(loUnknownPacket.BuffertoString(loReceiveBuffer.getArray() ));
        			
        			
					loUnknownPacket.readMessage(loReceiveBuffer);

	                if ( loUnknownPacket.isDataCrc8Correct() ) {
		                logger.info("wow, message correct..... ");
		                
	        			loState = ENOCEAN_MSG_STATE.GET_SYNC_STATE; 
		                loReceiveBuffer.clear();
	                }
	                else
	                {
		                logger.info("invalid CRC8D, reset and start over. ");
	        			loState = ENOCEAN_MSG_STATE.GET_SYNC_STATE; 
		                loReceiveBuffer.clear();
	                }

        		
        		} catch (Exception e) {
	                logger.log( Level.WARNING ,"invalid data, reset and start over. ", e);
        			loState = ENOCEAN_MSG_STATE.GET_SYNC_STATE; 
	                loReceiveBuffer.clear();
				}
        		
        		break;
        	}
        	
        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.log(Level.WARNING, "Error on serial io");
		}
    }

    public void sendRadioSubTel() {

    }

    public void receiveRadioSubTel() {

    }

}