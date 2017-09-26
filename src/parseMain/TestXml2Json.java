package parseMain;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by chengsheng on 2015/8/19.
 */
public class TestXml2Json {
    public static void main(String[] args) throws Exception {
        String xmlStr= readFile("C:\\Users\\Administrator\\Desktop\\xml\\content.xml");
        Document doc= DocumentHelper.parseText(xmlStr);
        JSONObject json=new JSONObject();
        dom4j2Json(doc.getRootElement(),json);
      JSONObject obj=json.getJSONObject("sheet").getJSONObject("topic");
      analysisJson(obj);
//      Topic s=new Topic(obj.getString("@id"), obj.getString("title"), new ArrayList<Topic>());
//      Object o=  obj.getJSONObject("children").getJSONObject("topics").get("topic");
//      if(o instanceof JSONObject){//如果此元素已存在,则转为jsonArray
//          JSONObject jsono=(JSONObject)o;
//          Topic s1=new Topic(jsono.getString("@id"), jsono.getString("title"), new ArrayList<Topic>());
//          s.getChildren().add(s1);
//          jsono.
//      }
      
//      if(o instanceof JSONArray){
//          jsona=(JSONArray)o;
//          jsona.add(chdjson);
//      }
      
      
      
      
      
//        System.out.println(JSONObject.toJSONString(s));

    }
    
    public static void  analysisJson(Object objJson){  
        //如果obj为json数组  
        if(objJson instanceof JSONArray){  
            JSONArray objArray = (JSONArray)objJson;  
            for (int i = 0; i < objArray.size(); i++) {  
                analysisJson(objArray.get(i));  
            }  
        }  
        //如果为json对象  
        else if(objJson instanceof JSONObject){  
            JSONObject jsonObject = (JSONObject)objJson;  
            Set<String> keyset = jsonObject.keySet();
          for (String keyTemp : keyset) {
        	
              Object object = jsonObject.get(keyTemp);  
              //如果得到的是数组  
              if(object instanceof JSONArray){  
                  JSONArray objArray = (JSONArray)object;  
                  analysisJson(objArray);  
              }  
              //如果key中是一个json对象  
              else if(object instanceof JSONObject){  
                  analysisJson((JSONObject)object);  
              }  
              //如果key中是其他  
              else{  
                  System.out.println("["+keyTemp+"]:"+object.toString()+" ");  
              }  
		}
            
        }  
    }  

    public static String readFile(String path) throws Exception {
        File file=new File(path);
        FileInputStream fis = new FileInputStream(file);
        FileChannel fc = fis.getChannel();
        ByteBuffer bb = ByteBuffer.allocate(new Long(file.length()).intValue());
        //fc向buffer中读入数据
        fc.read(bb);
        bb.flip();
        String str=new String(bb.array(),"UTF8");
        fc.close();
        fis.close();
        return str;

    }
    /**
     * xml转json
     * @param xmlStr
     * @return
     * @throws DocumentException
     */
    public static JSONObject xml2Json(String xmlStr) throws DocumentException{
        Document doc= DocumentHelper.parseText(xmlStr);
        JSONObject json=new JSONObject();
        dom4j2Json(doc.getRootElement(), json);
        return json;
    }

    /**
     * xml转json
     * @param element
     * @param json
     */
    public static void dom4j2Json(Element element,JSONObject json){
        //如果是属性
        for(Object o:element.attributes()){
            Attribute attr=(Attribute)o;
            if(!isEmpty(attr.getValue())){
            	if(attr.getName().equals("id")){
            	      json.put("@"+attr.getName(), attr.getValue());
            	}
            }
        }
        List<Element> chdEl=element.elements();
        if(chdEl.isEmpty()&&!isEmpty(element.getText())){//如果没有子元素,只有一个值
            json.put(element.getName(), element.getText());
        }
        if(element.getParent().getParent()!=null){
        	System.out.println(element.getParent().getParent().asXML());
        }
        

        for(Element e:chdEl){//有子元素
            if(!e.elements().isEmpty()){//子元素也有子元素
                JSONObject chdjson=new JSONObject();
                if(e.getName().equals("extensions")){
                	continue;
                }
                dom4j2Json(e,chdjson);
                Object o=json.get(e.getName());
                if(o!=null){
                    JSONArray jsona=null;
                    if(o instanceof JSONObject){//如果此元素已存在,则转为jsonArray
                        JSONObject jsono=(JSONObject)o;
                        json.remove(e.getName());
                        jsona=new JSONArray();
                        jsona.add(jsono);
                        jsona.add(chdjson);
                    }
                    if(o instanceof JSONArray){
                        jsona=(JSONArray)o;
                        jsona.add(chdjson);
                    }
                    json.put(e.getName(), jsona);
                }else{
                    if(!chdjson.isEmpty()){
                        json.put(e.getName(), chdjson);
                    }
                }


            }else{//子元素没有子元素
                for(Object o:element.attributes()){
                    Attribute attr=(Attribute)o;
                    if(!isEmpty(attr.getValue())){
                    	if(attr.getName().equals("id")){
                  	      json.put("@"+attr.getName(), attr.getValue());
                  	}
                    }
                }
                if(!e.getText().isEmpty()){
                    json.put(e.getName(), e.getText());
                }
            }
        }
    }

    public static boolean isEmpty(String str) {

        if (str == null || str.trim().isEmpty() || "null".equals(str)) {
            return true;
        }
        return false;
    }
}