package proxymusic;

import java.math.BigInteger;

public interface GuidoConvertable {

	public BigInteger getStaff();
	public java.lang.String getVoice();
    public java.lang.String convertToGuido();
	
}
