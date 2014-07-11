 
package com.zxsoft.crawler.storage;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.HashMap;
import org.apache.avro.Protocol;
import org.apache.avro.Schema;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Protocol;
import org.apache.avro.util.Utf8;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.FixedSize;
import org.apache.avro.specific.SpecificExceptionBase;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.specific.SpecificFixed;

@SuppressWarnings("all")
public class WebPage  {
  public static final Schema _SCHEMA = Schema.parse("{\"type\":\"record\",\"name\":\"WebPage\",\"namespace\":\"org.apache.nutch.storage\",\"fields\":[{\"name\":\"baseUrl\",\"type\":\"string\"},{\"name\":\"status\",\"type\":\"int\"},{\"name\":\"fetchTime\",\"type\":\"long\"},{\"name\":\"prevFetchTime\",\"type\":\"long\"},{\"name\":\"fetchInterval\",\"type\":\"int\"},{\"name\":\"retriesSinceFetch\",\"type\":\"int\"},{\"name\":\"modifiedTime\",\"type\":\"long\"},{\"name\":\"prevModifiedTime\",\"type\":\"long\"},{\"name\":\"protocolStatus\",\"type\":{\"type\":\"record\",\"name\":\"ProtocolStatus\",\"fields\":[{\"name\":\"code\",\"type\":\"int\"},{\"name\":\"args\",\"type\":{\"type\":\"array\",\"items\":\"string\"}},{\"name\":\"lastModified\",\"type\":\"long\"}]}},{\"name\":\"content\",\"type\":\"bytes\"},{\"name\":\"contentType\",\"type\":\"string\"},{\"name\":\"prevSignature\",\"type\":\"bytes\"},{\"name\":\"signature\",\"type\":\"bytes\"},{\"name\":\"title\",\"type\":\"string\"},{\"name\":\"text\",\"type\":\"string\"},{\"name\":\"parseStatus\",\"type\":{\"type\":\"record\",\"name\":\"ParseStatus\",\"fields\":[{\"name\":\"majorCode\",\"type\":\"int\"},{\"name\":\"minorCode\",\"type\":\"int\"},{\"name\":\"args\",\"type\":{\"type\":\"array\",\"items\":\"string\"}}]}},{\"name\":\"score\",\"type\":\"float\"},{\"name\":\"reprUrl\",\"type\":\"string\"},{\"name\":\"headers\",\"type\":{\"type\":\"map\",\"values\":\"string\"}},{\"name\":\"outlinks\",\"type\":{\"type\":\"map\",\"values\":\"string\"}},{\"name\":\"inlinks\",\"type\":{\"type\":\"map\",\"values\":\"string\"}},{\"name\":\"markers\",\"type\":{\"type\":\"map\",\"values\":\"string\"}},{\"name\":\"metadata\",\"type\":{\"type\":\"map\",\"values\":\"bytes\"}},{\"name\":\"batchId\",\"type\":\"string\"}]}");
  public static enum Field {
    BASE_URL(0,"baseUrl"),
    STATUS(1,"status"),
    FETCH_TIME(2,"fetchTime"),
    PREV_FETCH_TIME(3,"prevFetchTime"),
    FETCH_INTERVAL(4,"fetchInterval"),
    RETRIES_SINCE_FETCH(5,"retriesSinceFetch"),
    MODIFIED_TIME(6,"modifiedTime"),
    PREV_MODIFIED_TIME(7,"prevModifiedTime"),
    PROTOCOL_STATUS(8,"protocolStatus"),
    CONTENT(9,"content"),
    CONTENT_TYPE(10,"contentType"),
    TEXT(14,"text"),
    PARSE_STATUS(15,"parseStatus"),
    HEADERS(18,"headers"),
    METADATA(22,"metadata"),
    ;
    private int index;
    private String name;
    Field(int index, String name) {this.index=index;this.name=name;}
    public int getIndex() {return index;}
    public String getName() {return name;}
    public String toString() {return name;}
  };
  public static final String[] _ALL_FIELDS = {"baseUrl","status","fetchTime","prevFetchTime","fetchInterval","retriesSinceFetch","modifiedTime","prevModifiedTime","protocolStatus","content","contentType","prevSignature","signature","title","text","parseStatus","score","reprUrl","headers","outlinks","inlinks","markers","metadata","batchId",};
  private Utf8 baseUrl;
  private int status;
  private long fetchTime;
  private long prevFetchTime;
  private int fetchInterval;
  private int retriesSinceFetch;
  private long modifiedTime;
  private long prevModifiedTime;
  private ByteBuffer content;
  private Utf8 contentType;
  private Utf8 text;
  private Map<Utf8,Utf8> headers;
  private Map<Utf8,ByteBuffer> metadata;
  private Utf8 batchId;
  public WebPage() {
	  headers = new HashMap<Utf8,Utf8>();
  }
  public Schema getSchema() { return _SCHEMA; }
  public Object get(int _field) {
    switch (_field) {
    case 0: return baseUrl;
    case 1: return status;
    case 2: return fetchTime;
    case 3: return prevFetchTime;
    case 4: return fetchInterval;
    case 5: return retriesSinceFetch;
    case 6: return modifiedTime;
    case 7: return prevModifiedTime;
    case 9: return content;
    case 10: return contentType;
    case 14: return text;
    case 18: return headers;
    case 22: return metadata;
    default: throw new AvroRuntimeException("Bad index");
    }
  }
  @SuppressWarnings(value="unchecked")
  public void put(int _field, Object _value) {
    switch (_field) {
    case 0:baseUrl = (Utf8)_value; break;
    case 1:status = (Integer)_value; break;
    case 2:fetchTime = (Long)_value; break;
    case 3:prevFetchTime = (Long)_value; break;
    case 4:fetchInterval = (Integer)_value; break;
    case 5:retriesSinceFetch = (Integer)_value; break;
    case 6:modifiedTime = (Long)_value; break;
    case 7:prevModifiedTime = (Long)_value; break;
    case 9:content = (ByteBuffer)_value; break;
    case 10:contentType = (Utf8)_value; break;
    case 14:text = (Utf8)_value; break;
    case 22:metadata = (Map<Utf8,ByteBuffer>)_value; break;
    case 23:batchId = (Utf8)_value; break;
    default: throw new AvroRuntimeException("Bad index");
    }
  }
  public Utf8 getBaseUrl() {
    return (Utf8) get(0);
  }
  public void setBaseUrl(Utf8 value) {
    put(0, value);
  }
  public int getStatus() {
    return (Integer) get(1);
  }
  public void setStatus(int value) {
    put(1, value);
  }
  public long getFetchTime() {
    return (Long) get(2);
  }
  public void setFetchTime(long value) {
    put(2, value);
  }
  public long getPrevFetchTime() {
    return (Long) get(3);
  }
  public void setPrevFetchTime(long value) {
    put(3, value);
  }
  public int getFetchInterval() {
    return (Integer) get(4);
  }
  public void setFetchInterval(int value) {
    put(4, value);
  }
  public int getRetriesSinceFetch() {
    return (Integer) get(5);
  }
  public void setRetriesSinceFetch(int value) {
    put(5, value);
  }
  public long getModifiedTime() {
    return (Long) get(6);
  }
  public void setModifiedTime(long value) {
    put(6, value);
  }
  public long getPrevModifiedTime() {
    return (Long) get(7);
  }
  public void setPrevModifiedTime(long value) {
    put(7, value);
  }
  public ByteBuffer getContent() {
    return (ByteBuffer) get(9);
  }
  public void setContent(ByteBuffer value) {
    put(9, value);
  }
  public Utf8 getContentType() {
    return (Utf8) get(10);
  }
  public void setContentType(Utf8 value) {
    put(10, value);
  }
  public ByteBuffer getPrevSignature() {
    return (ByteBuffer) get(11);
  }
  public void setPrevSignature(ByteBuffer value) {
    put(11, value);
  }
  public ByteBuffer getSignature() {
    return (ByteBuffer) get(12);
  }
  public void setSignature(ByteBuffer value) {
    put(12, value);
  }
  public Utf8 getTitle() {
    return (Utf8) get(13);
  }
  public void setTitle(Utf8 value) {
    put(13, value);
  }
  public Utf8 getText() {
    return (Utf8) get(14);
  }
  public void setText(Utf8 value) {
    put(14, value);
  }
  public float getScore() {
    return (Float) get(16);
  }
  public void setScore(float value) {
    put(16, value);
  }
  public Utf8 getReprUrl() {
    return (Utf8) get(17);
  }
  public void setReprUrl(Utf8 value) {
    put(17, value);
  }
  @SuppressWarnings("unchecked")
  public Map<Utf8, Utf8> getHeaders() {
    return (Map<Utf8, Utf8>) get(18);
  }
  public Utf8 getFromHeaders(Utf8 key) {
    if (headers == null) { return null; }
    return headers.get(key);
  }
  public void putToHeaders(Utf8 key, Utf8 value) {
    headers.put(key, value);
  }
  @SuppressWarnings("unchecked")
  public Map<Utf8, Utf8> getOutlinks() {
    return (Map<Utf8, Utf8>) get(19);
  }
  @SuppressWarnings("unchecked")
  public Map<Utf8, Utf8> getInlinks() {
    return (Map<Utf8, Utf8>) get(20);
  }
  @SuppressWarnings("unchecked")
  public Map<Utf8, Utf8> getMarkers() {
    return (Map<Utf8, Utf8>) get(21);
  }
  @SuppressWarnings("unchecked")
  public Map<Utf8, ByteBuffer> getMetadata() {
    return (Map<Utf8, ByteBuffer>) get(22);
  }
  public ByteBuffer getFromMetadata(Utf8 key) {
    if (metadata == null) { return null; }
    return metadata.get(key);
  }
  public Utf8 getBatchId() {
    return (Utf8) get(23);
  }
  public void setBatchId(Utf8 value) {
    put(23, value);
  }
}
