package padtools.core.view;

import padtools.core.models.*;

import java.util.HashMap;
import java.util.Map;

/**
 * ロジックからViewを生成する
 * @author monaou
 */
public class Model2View {
    public final Object KEY_DEFAULT = new Object();
    public final Object KEY_MAIN = new Object();
    public final Object KEY_TEXT = new Object();
    private final ViewOption defaultOption = new ViewOption();
    private final Map<Object, ViewOption> optionMap = new HashMap<Object, ViewOption>();
    
    public Model2View(){
        ViewOption optMain = new ViewOption(defaultOption);
        optMain.setMargin(new ViewOption.Insets2D(10, 10, 10, 10));
        optionMap.put(KEY_MAIN, optMain);
        optionMap.put(KEY_DEFAULT, defaultOption);
    }
    
    public Map<Object, ViewOption> getOptionMap(){
        return optionMap;
    }
    
    private ViewOption getOption(Object key1, Object key2){
        ViewOption opt = optionMap.get(key1);
        if( opt != null) return opt;
        opt = optionMap.get(key2);
        return opt == null ? defaultOption : opt;
    }

    private View NodeToView(NodeBase node){
        View view;
        View subView = null;
        if(node != null &&  WithChildNode.class.isInstance(node) ){
            WithChildNode wn = (WithChildNode)node;
            subView = NodeToView(wn.getChildNode());
        }

        if(node instanceof LoopNode){
            LoopNode ln = (LoopNode)node;
            view = new BoxView(
                    getOption(node, LoopNode.class),
                    new TextView(getOption(null, KEY_TEXT),
                    ln.getText()),
                    subView,
                    BoxView.BorderType.Box,
                    ln.isWhile(), !ln.isWhile());
        }
        else if(node instanceof CallNode){
            CallNode cn = (CallNode)node;
            view = new BoxView(
                    getOption(node, CallNode.class),
                    new TextView(getOption(null, KEY_TEXT),
                            cn.getText()),
                    subView,
                    BoxView.BorderType.Box,
                    true, true);
        }
        else if(node instanceof TerminalNode){
            TerminalNode tn = (TerminalNode)node;
            view = new BoxView(
                    getOption(node, TerminalNode.class),
                    new TextView(getOption(null, KEY_TEXT),
                            tn.getText()),
                    subView,
                    BoxView.BorderType.WRounded,
                    false, false);
        }
        else if(node instanceof CommentNode){
            CommentNode cn = (CommentNode)node;
            view = new BoxView(
                    getOption(node, CommentNode.class),
                    new TextView(getOption(null, KEY_TEXT),
                    "(" + cn.getText() + ")"),
                    subView,
                    BoxView.BorderType.None,
                    false, false);
        }
        else if(node instanceof SwitchNode){
            SwitchNode sn = (SwitchNode)node;
            SwitchView sv = new SwitchView(
                    getOption(node, SwitchNode.class),
                    new TextView(getOption(sn.getText(), KEY_TEXT),
                    sn.getText()));
            view = sv;
            
            for(Map.Entry<String, NodeBase> entry : sn.getCases().entrySet() ){
                sv.getCaseMap().put(
                        new TextView(getOption(entry.getKey(), KEY_TEXT), entry.getKey()),
                        NodeToView(entry.getValue()));
            }
        }
        else if(node instanceof IfNode){
            IfNode in = (IfNode)node;
            SwitchView sv = new SwitchView(
                    getOption(node, SwitchNode.class),
                    new TextView(getOption(null, KEY_TEXT),
                            in.getText()));
            view = sv;

            sv.getCaseMap().put(
                    new TextView(getOption(null, KEY_TEXT), " "),
                    NodeToView(in.getTrueNode()));
            sv.getCaseMap().put(
                    new TextView(getOption(null, KEY_TEXT), "  "),
                    NodeToView(in.getFalseNode()));
        }
        else if(node instanceof ProcessNode){
            ProcessNode pn = (ProcessNode)node;
            view = new BoxView(
                    getOption(node, NodeBase.class),
                    new TextView(getOption(null, KEY_TEXT),
                    pn.getText()),
                    subView,
                    BoxView.BorderType.Box,
                    false, false);
        }
        else if(node instanceof NodeListNode){
            NodeListNode nn = (NodeListNode)node;
            ViewListView vl = new ViewListView(getOption(node, NodeListNode.class));
            for(NodeBase nb : nn.getChildren()){
                vl.getViewList().add(NodeToView(nb));
            }
            view = vl;
        }
        else {
            return null;
        }
        
        view.setTag(view);
        return view;
    }

    /**
     * ビューを生成する。
     * @param model
     * @return 
     */
    public View toView(PADModel model){
        View mainView = NodeToView(model.getTopNode());
        mainView.setViewOption(getOption(null, KEY_MAIN));
        return mainView;
    }
}
