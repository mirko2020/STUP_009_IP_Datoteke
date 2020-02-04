package yi.etf.studiranje.datoteke.gui.font;

import javafx.scene.text.Font;

/**
 * Управљање рукопи�?има. 
 * @author Computer
 *
 */
public final class FontUtil {
	private FontUtil() {}
	
	public static Font getCourierNew() {
		return Font.loadFont(FontUtil.class.getResourceAsStream("/yi/etf/studiranje/datoteke/gui/font/cour.ttf"),12);
	}
	public static Font getCourierNewBold() {
		return Font.loadFont(FontUtil.class.getResourceAsStream("/yi/etf/studiranje/datoteke/gui/font/courbd.ttf"),12); 
	}
}
