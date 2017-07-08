package padtools.core.formats.spd;

import padtools.core.models.*;

import java.io.*;
import java.util.LinkedList;
import java.util.regex.Pattern;

/**
 * SPDParser(Simple PAD Description format)形式を扱う静的クラス。
 */
public class SPDParser {

    /**
     * インスタンス化できないコンストラクタ。
     */
    private SPDParser(){}

    /**
     * 何もしないダミーのエラーレシーバ。
     */
    private static class DummyParseErrorReceiver implements ParseErrorReceiver{

        @Override
        public boolean receiveParseError(String lineStr, int lineNo, ParseErrorException err) {
            return true;
        }
    }

    /**
     * パース中のコンテキストを扱うクラス。
     */
    private static class Context {
        public enum OptionStatus{
            Default,
            Else
        }

        /* 親のコンテキスト */
        public Context parent = null;

        /* 深さ */
        public int depth = 0;

        /* ノードリスト */
        public final LinkedList<NodeBase> nodeList = new LinkedList<NodeBase>();

        /* コンテキストの追加状態 */
        public OptionStatus optionStatus = OptionStatus.Default;

        /* コンテキストの状態に結びつく引数 */
        public String optionArg = null;
    }

    //コメントを判定する正規表現オブジェクト
    private final static Pattern patternComment = Pattern.compile("^\\s*(#.*)?$");

    /**
     * 本文を処理する
     * @param context 現在のコンテキスト
     * @param body 本文
     */
    private static void handleBody(Context context, String body) throws ParseErrorException{
        //状態の制御
        if(context.nodeList.size() > 0){
            NodeBase lnode = context.nodeList.getLast();
            if( context.optionArg != null && SwitchNode.class.isInstance(lnode)){
                SwitchNode node = (SwitchNode)lnode;
                node.getCases().put(context.optionArg, null);
                context.optionArg = null;
            }
        }

        if(body.startsWith(":")){
            String[] ss = body.split("[ \t]+");

            //コマンド部分と引数部分を分離
            String cmd = ss[0].substring(1);
            String arg = ss.length > 1 ? body.substring(ss[0].length()).trim() : null;

            if( "call".equals(cmd) ){
                if(arg == null){
                    throw new RequireArgumentException();
                }
                CallNode node = new CallNode();
                node.setText(arg);
                context.nodeList.add(node);
                context.optionStatus = Context.OptionStatus.Default;
                context.optionArg = null;
            }
            else if("terminal".equals(cmd)){
                if(arg == null){
                    throw new RequireArgumentException();
                }
                TerminalNode node = new TerminalNode();
                node.setText(arg);
                context.nodeList.add(node);
                context.optionStatus = Context.OptionStatus.Default;
                context.optionArg = null;
            }
            else if("comment".equals(cmd)){
                if(arg == null){
                    throw new RequireArgumentException();
                }
                CommentNode node = new CommentNode();
                node.setText(arg);
                context.nodeList.add(node);
                context.optionStatus = Context.OptionStatus.Default;
                context.optionArg = null;
            }
            else if("while".equals(cmd)){
                if(arg == null){
                    throw new RequireArgumentException();
                }
                LoopNode node = new LoopNode();
                node.setWhile(true);
                node.setText(arg);
                context.nodeList.add(node);
                context.optionStatus = Context.OptionStatus.Default;
                context.optionArg = null;
            }
            else if("dowhile".equals(cmd)){
                if(arg == null){
                    throw new RequireArgumentException();
                }
                LoopNode node = new LoopNode();
                node.setWhile(false);
                node.setText(arg);
                context.nodeList.add(node);
                context.optionStatus = Context.OptionStatus.Default;
                context.optionArg = null;
            }
            else if("if".equals(cmd)){
                if(arg == null){
                    throw new RequireArgumentException();
                }
                IfNode node = new IfNode();
                node.setText(arg);
                context.nodeList.add(node);
                context.optionStatus = Context.OptionStatus.Default;
                context.optionArg = null;
            }
            else if("switch".equals(cmd)){
                if(arg == null){
                    throw new RequireArgumentException();
                }
                SwitchNode node = new SwitchNode();
                node.setText(arg);
                context.nodeList.add(node);
                context.optionStatus = Context.OptionStatus.Default;
                context.optionArg = null;
            }
            else if("else".equals(cmd)){
                NodeBase node = context.nodeList.size() == 0 ? null : context.nodeList.getLast();
                if(node == null || !IfNode.class.isInstance(node)){
                    throw new UnexpectedElseException();
                }
                if(arg != null){
                    throw new NotRequireArgumentException();
                }
                context.optionStatus = Context.OptionStatus.Else;
                context.optionArg = null;
            }
            else if("case".equals(cmd)){
                NodeBase node = context.nodeList.size() == 0 ? null : context.nodeList.getLast();
                if(node == null || !SwitchNode.class.isInstance(node)){
                    throw new UnexpectedCaseException();
                }
                if(arg == null){
                    throw new RequireArgumentException();
                }
                if( ((SwitchNode)node).getCases().containsKey(arg) ){
                    throw new CaseDuplicateException();
                }
                context.optionStatus = Context.OptionStatus.Default;
                context.optionArg = arg;
            }
            else{
                throw new UnknownCommandException();
            }
        }
        else{
            ProcessNode node = new ProcessNode();
            node.setText(body);
            context.nodeList.add(node);
            context.optionStatus = Context.OptionStatus.Default;
            context.optionArg = null;
        }
    }

    /**
     * 親コンテキストのノードリストの末尾にノードを追加し、親コンテキストを返す。
     * @param context コンテキスト
     * @return 親のコンテキスト
     */
    private static Context upToParent(Context context) throws ParseErrorException{
        if(context == null){
            return null;
        }

        //状態の制御
        if(context.nodeList.size() > 0){
            NodeBase lnode = context.nodeList.getLast();
            if( context.optionArg != null && SwitchNode.class.isInstance(lnode)){
                SwitchNode node = (SwitchNode)lnode;
                node.getCases().put(context.optionArg, null);
                context.optionArg = null;
            }
        }

        if(context.parent == null ){
            return null;
        }

        //追加するノードを新規作成。
        NodeBase newNode;
        if(context.nodeList.size() == 0){
            return context.parent;
        }
        else if(context.nodeList.size() == 1){
            newNode = context.nodeList.get(0);
        }
        else if(context.nodeList.size() > 1){
            NodeListNode nodeList = new NodeListNode();
            nodeList.getChildren().addAll(context.nodeList);
            newNode = nodeList;
        }
        else{
            throw new UnexpectedInnerExpection("Parent node is not found");
        }

        //ノードの追加先となるノード。
        NodeBase pnode = context.parent.nodeList.getLast();

        //ノードの種類に応じてノードの追加先に追加。
        if(WithChildNode.class.isInstance(pnode)){
            WithChildNode node = (WithChildNode)pnode;
            node.setChildNode(newNode);
        }
        else if(SwitchNode.class.isInstance(pnode)){
            SwitchNode node = (SwitchNode)pnode;
            node.getCases().put(context.parent.optionArg, newNode);
        }
        else if(IfNode.class.isInstance(pnode)){
            IfNode node = (IfNode)pnode;
            if(context.parent.optionStatus == Context.OptionStatus.Default){
                node.setTrueNode(newNode);
            }
            else if(context.parent.optionStatus == Context.OptionStatus.Else){
                node.setFalseNode(newNode);
            }
            else {
                throw new UnexpectedInnerExpection("Illegal option status");
            }
        }
        else{
            throw new UnexpectedInnerExpection("Illegal command");
        }

        //親ノードの状態をリセットする。
        context.parent.optionStatus = Context.OptionStatus.Default;
        context.parent.optionArg = null;

        //親ノードを返す。
        return context.parent;
    }

    /**
     * 文字列からPADモデルを生成する。
     * @param src SPD形式の文字列
     * @return PADモデル
     */
    public static PADModel parse(String src, ParseErrorReceiver exr){
        if(src == null) throw new IllegalArgumentException("src is null");

        //エラーレシーバがnullの場合はダミーを使用する
        if(exr == null) exr = new DummyParseErrorReceiver();

        //先頭のコンテキスト
        Context rootContext = new Context();
        //現在のコンテキスト
        Context context = rootContext;

        //終了フラグ
        boolean errExit = false;

        //１行づつ読み込む
        final BufferedReader br = new BufferedReader(new StringReader(src));
        String line;
        int lineNo = 0;
        try{
            while((line = br.readLine()) != null){
                lineNo++;

                //コメント行は読み飛ばし
                if(patternComment.matcher(line).matches()) continue;

                //先頭のタブ数を数える。
                int tabNum = 0;
                for(int i=0; i<line.length(); ++i){
                    if( line.charAt(i) == '\t' ){
                        tabNum++;
                    }
                    else{
                        break;
                    }
                }
                try{
                    //子コンテキストの作成処理を行う。
                    if((tabNum > 0 && context.nodeList.size() == 0) || tabNum < 0){
                        //最初からタブがある場合は不正。
                        throw new IllegalIndentException();
                    }
                    if(tabNum > context.depth){
                        //タブが増加した場合の処理

                        //正当性をチェックする。
                        NodeBase parentNode = context.nodeList.getLast();
                        if( tabNum > context.depth + 1 || CommentNode.class.isInstance(parentNode)){
                            //親がコメントか２階層以上離れているのは不正。
                            throw new IllegalIndentException();
                        }
                        if( SwitchNode.class.isInstance(parentNode) && context.optionArg == null){
                            //子を持たないタイプの場合は不正。
                            throw new IllegalIndentException();
                        }

                        //子コンテキストを生成する。
                        Context newContext = new Context();
                        newContext.parent = context;
                        newContext.depth = context.depth + 1;
                        context = newContext;
                    }

                    //タブが減少した際の処理
                    while(tabNum < context.depth){
                        context = upToParent(context);
                    }

                    //本文は行のデータをtrimしたものとする。
                    //行末が ¥ の場合は複数行扱いとする。
                    String body = line.trim();
                    if(body.endsWith("@")){
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        pw.println(body.substring(0, body.length() - 1));
                        while((line = br.readLine()) != null){
                            lineNo++;

                            //コメント行で止まる
                            if(patternComment.matcher(line).matches()) break;

                            //行末に @ が間読み込む
                            body = line.trim();
                            if(body.endsWith("@")){
                                pw.println(body.substring(0, body.length() - 1));
                            }
                            else{
                                pw.print(body);
                                break;
                            }

                        }
                        body = sw.toString();
                    }
                    body = body.replaceAll("@", "\n");

                    //本文を処理する。
                    handleBody(context, body);
                }
                catch(ParseErrorException ex){
                    if(exr.receiveParseError(line, lineNo-1, ex)){
                        continue;
                    }
                    else{
                        errExit = true;
                        break;
                    }
                }


            }

            //先頭まで戻る
            while(!errExit && context != null){
                try{
                   context = upToParent(context);
                }
                catch(ParseErrorException ex){
                    exr.receiveParseError(line, lineNo-1, ex);
                    errExit = true;
                }
            }
        }
        catch(IOException ex){
            exr.receiveParseError("", lineNo, new UnexpectedIOException());
            errExit = true;
        }

        //途中で解析が終了した場合は、nullを返す。
        if(errExit) return null;

        //返却するモデルのインスタンスを生成
        final PADModel model = new PADModel();
        if(rootContext.nodeList.size() == 0){
            model.setTopNode(null);
        }
        else if(rootContext.nodeList.size() == 1){
            model.setTopNode(rootContext.nodeList.get(0));
        }
        else{
            NodeListNode topNode = new NodeListNode();
            topNode.getChildren().addAll(rootContext.nodeList);
            model.setTopNode(topNode);
        }

        return model;
    }
}
