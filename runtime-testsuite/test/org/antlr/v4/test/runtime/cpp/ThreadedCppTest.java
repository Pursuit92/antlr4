package org.antlr.v4.test.runtime.cpp;

import org.stringtemplate.v4.ST;

public class ThreadedCppTest extends BaseCppTest {
	protected void writeParserTestFile(String parserName, String lexerName,
	                                   String listenerName, String visitorName,
	                                   String parserStartRuleName, boolean debug, boolean trace) {
		if(!parserStartRuleName.endsWith(")"))
			parserStartRuleName += "()";
		ST outputFileST = new ST(
			"#include \\<iostream>\n"
        + "#include \\<thread>\n"
				+ "\n"
				+ "#include \"antlr4-runtime.h\"\n"
				+ "#include \"<lexerName>.h\"\n"
				+ "#include \"<parserName>.h\"\n"
				+ "\n"
        + "using namespace std;\n"
				+ "using namespace antlr4;\n"
				+ "\n"
				+ "class TreeShapeListener : public tree::ParseTreeListener {\n"
				+ "public:\n"
				+ "  void visitTerminal(tree::TerminalNode *) override {}\n"
				+ "  void visitErrorNode(tree::ErrorNode *) override {}\n"
				+ "  void exitEveryRule(ParserRuleContext *) override {}\n"
				+ "  void enterEveryRule(ParserRuleContext *ctx) override {\n"
				+ "    for (auto child : ctx->children) {\n"
				+ "      tree::ParseTree *parent = child->parent;\n"
				+ "      ParserRuleContext *rule = dynamic_cast\\<ParserRuleContext *>(parent);\n"
				+ "      if (rule != ctx) {\n"
				+ "        throw \"Invalid parse tree shape detected.\";\n"
				+ "      }\n"

				+ "    }\n"
				+ "  }\n"
				+ "};\n"
				+ "\n"
				+ "\n"
        + "void run_parser(const char * inputString) {\n"
        + "  ANTLRInputStream input(inputString);\n"
        + "  <lexerName> lexer(&input);\n"
        + "  CommonTokenStream tokens(&lexer);\n"
        + "<createParser>"
        + "\n"
        + "  tree::ParseTree *tree = parser.<parserStartRuleName>;\n"
        + "  TreeShapeListener listener;\n"
        + "  tree::ParseTreeWalker::DEFAULT.walk(&listener, tree);\n"
        + "\n"
        + "}\n"
        + "\n"
        + "void thread_main(const char * fileName) {\n"
        + "  ifstream inputFile(fileName);\n"
        + "  stringstream buf;\n"
        + "  buf \\<\\< inputFile.rdbuf();\n"
        + "  string inputString = buf.str();\n"
        + "  for(int i = 0; i \\< 50; ++i) {\n"
        + "    run_parser(inputString.c_str());\n"
        + "  }\n"
        + "}\n"
				+ "int main(int argc, const char* argv[]) {\n"
        + "  thread one(thread_main, argv[1]);\n"
        + "  thread two(thread_main, argv[1]);\n"
        + "  thread three(thread_main, argv[1]);\n"
        + "  thread four(thread_main, argv[1]);\n"
        + "\n"
        + "  one.join();\n"
        + "  two.join();\n"
        + "  three.join();\n"
        + "  four.join();\n"
				+ "  return 0;\n"
				+ "}\n"
		);

		String stSource = "  <parserName> parser(&tokens);\n";
		if(debug) {
			stSource += "  DiagnosticErrorListener errorListener;\n";
			stSource += "  parser.addErrorListener(&errorListener);\n";
		}
		if(trace)
			stSource += "  parser.setTrace(true);\n";
		ST createParserST = new ST(stSource);
		outputFileST.add("createParser", createParserST);
		outputFileST.add("parserName", parserName);
		outputFileST.add("lexerName", lexerName);
		outputFileST.add("listenerName", listenerName);
		outputFileST.add("visitorName", visitorName);
		outputFileST.add("parserStartRuleName", parserStartRuleName);
		writeFile(tmpdir, "Test.cpp", outputFileST.render());
	}
}
