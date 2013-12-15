package model.modelement3;

import model.modelement3.ModContext.ModContextType;

/**
 *
 * @author Amineri
 */


public class ModOffsetToken extends ModToken
{
    private String operand;
    private int jumpoffset;
    
    ModOffsetToken(ModOperandElement o)
    {
        super(o);
        name = "ModJumpToken";
        operand = null;
        isSimpleString = false;
        setContextFlag(ModContextType.VALID_CODE, true);
    }


    ModOffsetToken(ModOperandElement o, String sParseItem)
    {
        super(o);
        operand = sParseItem;
        name = "ModRelativeJumpToken";
    }

	protected String parseUnrealHex(String s) {
		s = super.parseUnrealHex(s, 2);
		int int0 = Integer.getInteger(data.split("\\s")[0], 16);
		int int1 = Integer.getInteger(data.split("\\s")[1], 16);
		jumpoffset = 256 * int0 + int1;
		return s;
	}
    
    @Override
    public int getOffset()
    {
        return jumpoffset;
    }
}