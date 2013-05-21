package org.enocean.java.packets;

import org.enocean.java.utils.CRC8;
import org.enocean.java.utils.CircularByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Payload {
    private static Logger logger = LoggerFactory.getLogger(Payload.class);

    private byte[] data;
    private byte[] optionalData;
    private byte crc8;

    public static Payload from(Header header, CircularByteBuffer buffer) {
        logger.info("Reading payload...");
        Payload payload = new Payload();
        payload.setData(new byte[header.getDataLength()]);
        buffer.get(payload.getData());
        payload.setOptionalData(new byte[header.getOptionalDataLength()]);
        buffer.get(payload.getOptionalData());
        payload.crc8 = buffer.get();
        logger.info(payload.toString());
        payload.checkCrc8();
        return payload;
    }

    public Payload() {

    }

    public void initCRC8() {
        crc8 = calculateCrc8();
    }

    public void checkCrc8() {
        if (calculateCrc8() != crc8) {
            throw new RuntimeException("Payload CRC 8 is not correct! Expected " + calculateCrc8() + ", but received " + crc8);
        }
    }

    public byte[] toBytes() {
        ByteArrayWrapper bytes = new ByteArrayWrapper();
        bytes.addBytes(getData());
        bytes.addBytes(getOptionalData());
        bytes.addByte(crc8);
        return bytes.getArray();
    }

    public byte[] getData() {
        return data;
    }

    public byte[] getOptionalData() {
        return optionalData;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setOptionalData(byte[] optionalData) {
        this.optionalData = optionalData;
    }

    @Override
    public String toString() {
        return "Payload: " + "data=" + printByteArray(getData()) + ", optionaldata=" + printByteArray(getOptionalData()) + ", crc8d="
                + crc8;
    }

    private byte calculateCrc8() {
        CRC8 crc8 = new CRC8();
        crc8.update(getData(), 0, getData().length);
        crc8.update(getOptionalData(), 0, getOptionalData().length);
        return (byte) crc8.getValue();
    }

    private String printByteArray(byte[] data) {
        String s = "[";
        for (int i = 0; i < data.length; i++) {
            if (i != 0) {
                s += ", ";
            }
            s += data[i];
        }
        s += "]";
        return s;
    }

}