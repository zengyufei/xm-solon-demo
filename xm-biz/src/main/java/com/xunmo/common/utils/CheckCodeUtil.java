package com.xunmo.common.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CheckCodeUtil {

	/**
	 * 定义图片的width
	 */
	private static int width = 90;

	/**
	 * 定义图片的height
	 */
	private static int height = 30;

	/**
	 * 定义图片上显示验证码的个数
	 */
	private static int codeCount = 4;

	private static int xx = 15;

	private static int fontHeight = 18;

	private static int codeY = 21;

	private static char[] codeSequence = { '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	/**
	 * 生成一个map集合 code为生成的验证码 codePic为生成的验证码BufferedImage对象
	 * @return
	 */
	public static Map<String, Object> generateCodeAndPic() {
		return generateCodeAndPic(20);
	}

	/**
	 * 生成验证码Map={code:'验证码', codePic: '验证码BufferedImage对象'}
	 * @param randomLineNum 干扰线数量
	 * @return 验证码Map={code:'验证码', codePic: '验证码BufferedImage对象'}
	 */
	public static Map<String, Object> generateCodeAndPic(int randomLineNum) {
		// 定义图像buffer
		BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// Graphics2D gd = buffImg.createGraphics();
		// Graphics2D gd = (Graphics2D) buffImg.getGraphics();
		Graphics gd = buffImg.getGraphics();
		// 创建一个随机数生成器类
		Random random = new Random();
		// 将图像填充为白色
		gd.setColor(Color.WHITE);
		gd.fillRect(0, 0, width, height);

		// 创建字体，字体的大小应该根据图片的高度来定。
		Font font = new Font("Fixedsys", Font.BOLD, fontHeight);
		// 设置字体。
		gd.setFont(font);

		// 画边框。
		gd.setColor(Color.BLACK);
		gd.drawRect(0, 0, width - 1, height - 1);

		// 随机产生20条干扰线，使图象中的认证码不易被其它程序探测到。
		gd.setColor(Color.BLACK);
		for (int i = 0; i < randomLineNum; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(12);
			int yl = random.nextInt(12);
			gd.drawLine(x, y, x + xl, y + yl);
		}

		// randomCode用于保存随机产生的验证码，以便用户登录后进行验证。
		StringBuffer randomCode = new StringBuffer();
		int red = 0, green = 0, blue = 0;

		// 随机产生codeCount数字的验证码。
		for (int i = 0; i < codeCount; i++) {
			// 得到随机产生的验证码数字。
			String code = String.valueOf(codeSequence[random.nextInt(codeSequence.length)]);
			// 产生随机的颜色分量来构造颜色值，这样输出的每位数字的颜色值都将不同。
			red = random.nextInt(255);
			green = random.nextInt(255);
			blue = random.nextInt(255);

			// 用随机产生的颜色将验证码绘制到图像中。
			gd.setColor(new Color(red, green, blue));
			gd.drawString(code, (i + 1) * xx, codeY);

			// 将产生的四个随机数组合在一起。
			randomCode.append(code);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		// 存放验证码
		map.put("code", randomCode);
		// 存放生成的验证码BufferedImage对象
		map.put("codePic", buffImg);
		return map;
	}

	public static void main(String[] args) throws Exception {
		// 创建文件输出流对象
		OutputStream out = new FileOutputStream("D://img/" + System.currentTimeMillis() + ".jpg");
		Map<String, Object> map = CheckCodeUtil.generateCodeAndPic();
		ImageIO.write((RenderedImage) map.get("codePic"), "jpeg", out);
		System.out.println("验证码的值为：" + map.get("code"));
	}

}
