package padtools.core.formats.json;

import padtools.core.formats.spd.UnexpectedInnerExpection;
import padtools.core.models.*;

import java.io.StringWriter;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: monaou
 * Date: 12/07/17
 * Time: 22:15
 * To change this template use File | Settings | File Templates.
 */
public class JsonEncoder {
    private JsonEncoder(){}

    private static String join(List<String> strs){
        if( strs.size() == 0){
            return "";
        }
        else if( strs.size() == 1 ){
            return strs.get(0);
        }
        else{
            StringWriter sw = new StringWriter();
            boolean first = true;
            for(String s : strs){
                if(!first) sw.append(",");
                sw.append(s);
                first = false;
            }
            return sw.toString();
        }
    }

    private static String escape(String str){
        return str.replaceAll("\n", "\\\\n");
    }

    private static String nodeToMap(NodeBase node){
        if(node == null){
            return "null";
        }

        HashMap<String, String> map = new LinkedHashMap<java.lang.String, java.lang.String>();
        HashMap<String, String> attr = new LinkedHashMap<String, String>();
        attr.put("type", NodeCatalog.getByClass(node.getClass()).getTypeName());
        node.toMap(attr);
        for(Map.Entry<String, String> entry : attr.entrySet()){
            map.put(escape(entry.getKey()), "\"" + escape(entry.getValue()) + "\"");
        }

        if(WithChildNode.class.isInstance(node)){
            WithChildNode wn = (WithChildNode)node;
            map.put("child", nodeToMap(wn.getChildNode()));
        }
        else if(NodeListNode.class.isInstance(node)){
            NodeListNode nln = (NodeListNode)node;
            List<String> strs = new LinkedList<String>();
            for(NodeBase n : nln.getChildren()){
                strs.add(nodeToMap(n));
            }
            map.put("children", "[" + join(strs) + "]");
        }
        else if(SwitchNode.class.isInstance(node)){
            SwitchNode sn = (SwitchNode)node;
            List<String> strs = new LinkedList<String>();
            for(Map.Entry<String, NodeBase> entry : sn.getCases().entrySet()){
                strs.add("\"" + escape(entry.getKey()) + "\":" + nodeToMap(entry.getValue()) );
            }
            map.put("cases", "{" + join(strs) + "}");
        }
        else if(IfNode.class.isInstance(node)){
            IfNode in = (IfNode)node;
            map.put("true", nodeToMap(in.getTrueNode()));
            map.put("false", nodeToMap(in.getFalseNode()));
        }

        List<String> rets = new LinkedList<String>();
        for(Map.Entry<String, String> entry : map.entrySet()){
            rets.add("\"" + escape(entry.getKey()) + "\":" + entry.getValue());
        }

        return "{" + join(rets) + "}";
    }

    public static String encode(PADModel model){
        NodeBase topNode = model.getTopNode();
        return nodeToMap(topNode);
    }
}
