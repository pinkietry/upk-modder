package model.modtree;

import model.modtree.ModContext.ModContextType;
import parser.unrealhex.OperandTable;

/**
 * TODO: API 
 * @author Amineri
 */
public class ModOperandNode extends ModTreeNode {

	/**
	 * TODO: API
	 */
    private String operand = "";
	
    /**
     * TODO: API
     * @param parent
     */
	public ModOperandNode(ModTreeNode parent) {
		super(parent);
		
		this.setContextFlag(ModContextType.HEX_CODE, true);
		this.setContextFlag(ModContextType.VALID_CODE, true);
	}

    @Override
	public String getName() {
		return "ModOperandNode_" + this.operand;
	}
	
	/**
	 * Overrides string naming for display via JTreePane
	 * @return
	 */
	@Override
	public String toString() {
		if(this.expanded) {
			return OperandTable.getOperandName(operand);
		} else {
			return super.toString();
		}
	}
    
    /**
     * Parses a passed string into ModTokens.
     * TODO -- optimize this code
     * @param s
     * @return - the unparsed string remnant
     */
	public String parseUnrealHex(String s) {
		return this.parseUnrealHex(s, 0);
	}
	
	@Override
	public String parseUnrealHex(String s, int num) {
		boolean isOperand = true;
		int lastEnd = this.getStartOffset();
		String operand = s.split("\\s")[0];
		if (operand.isEmpty()) {
			return "ERROR";
		}
		this.operand = operand;

		String sOpCodes = OperandTable.getOperandString(operand);
//		if (!operand.equalsIgnoreCase(sOpCodes.split("\\s", 2)[0])) {
//			System.out.println("/* opcode mismatch */");
//			return "ERROR";
//		}
//		s = s.split("\\s", 2)[1];
		String[] sParseItems = sOpCodes.split("\\s", 2)[1].split("\\s");
		for (String sParseItem : sParseItems) {
			sParseItem = sParseItem.toUpperCase();
			if (sParseItem.matches("[0-9]")) {
				ModGenericLeaf n;
				if (isOperand) {
					n = new ModGenericLeaf(this, true);
					isOperand = false;
				} else {
					n = new ModGenericLeaf(this);
				}
				n.setRange(lastEnd, lastEnd);
				addNode(n);
				s = n.parseUnrealHex(s, Integer.parseInt(sParseItem));
				lastEnd = n.getEndOffset();
				continue;
			} else if (sParseItem.equals("G")) {
				ModOperandNode n = new ModOperandNode(this);
				n.setRange(lastEnd, lastEnd);
				addNode(n);
				s = n.parseUnrealHex(s);
				lastEnd = n.getEndOffset();
				continue;
			} else if (sParseItem.equals("P")) {
				while (!s.split("\\s")[0].equals("16")) {
					ModOperandNode n = new ModOperandNode(this);
					n.setRange(lastEnd, lastEnd);
					addNode(n);
					s = n.parseUnrealHex(s);
					lastEnd = n.getEndOffset();
				}
				continue;
			} else if (sParseItem.equals("R")) {
				ModReferenceLeaf n = new ModReferenceLeaf(this, false);
				n.setRange(lastEnd, lastEnd);
				addNode(n);
				s = n.parseUnrealHex(s);
				lastEnd = n.getEndOffset();
				continue;
			} else if (sParseItem.equals("NR")) {
				ModReferenceLeaf n = new ModReferenceLeaf(this, true);
				n.setRange(lastEnd, lastEnd);
				addNode(n);
				s = n.parseUnrealHex(s);
				lastEnd = n.getEndOffset();
				continue;
			} else if (sParseItem.equals("N")) {
				ModStringLeaf n = new ModStringLeaf(this);
				n.setRange(lastEnd, lastEnd);
				addNode(n);
				s = n.parseUnrealHex(s);
				lastEnd = n.getEndOffset();
				continue;
			} else if (sParseItem.startsWith("S")) {
				ModOffsetLeaf n = new ModOffsetLeaf(this, sParseItem);
				n.setRange(lastEnd, lastEnd);
				addNode(n);
				s = n.parseUnrealHex(s);
				lastEnd = n.getEndOffset();
				continue;
			} else if (sParseItem.equals("J")) {
				ModOffsetLeaf n = new ModOffsetLeaf(this);
				n.setRange(lastEnd, lastEnd);
				addNode(n);
				s = n.parseUnrealHex(s);
				lastEnd = n.getEndOffset();
				continue;
			} else if (sParseItem.equals("C")) {
				if (s.split("\\s")[0].equalsIgnoreCase("FF") && s.split("\\s")[1].equalsIgnoreCase("FF")) {
					ModGenericLeaf n = new ModGenericLeaf(this);
					n.setRange(lastEnd, lastEnd);
					addNode(n);
					s = n.parseUnrealHex(s, Integer.parseInt(sParseItem));
					lastEnd = n.getEndOffset();
				} else {
					ModOffsetLeaf n1 = new ModOffsetLeaf(this);
					n1.setRange(lastEnd, lastEnd);
					addNode(n1);
					s = n1.parseUnrealHex(s);
					lastEnd = n1.getEndOffset();

					ModOperandNode n2 = new ModOperandNode(this);
					n2.setRange(lastEnd, lastEnd);
					addNode(n2);
					s = n2.parseUnrealHex(s);
					lastEnd = n2.getEndOffset();
				}
			}
		}
		this.setRange(this.getStartOffset(), lastEnd);
		return s;
	}

	@Override
	public void setText(String text) {
		// do nothing
	}

	@Override
	public String getText() {
		// return nothing
		return "";
	}
}
