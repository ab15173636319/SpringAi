package ai.s_ai.components;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Component
public class PromptXmlReader {

    /**
     * 按 name 读取单个 prompt 的全部字段
     */
    public Map<String, String> readPrompt(String name) {
        try (InputStream is = new ClassPathResource("prompt.xml").getInputStream()) {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            XPath xPath = XPathFactory.newInstance().newXPath();
            // 选中 name 对应的 <prompt> 元素
            Element el = (Element) xPath.evaluate("//prompt[@name='" + name + "']", doc, XPathConstants.NODE);
            if (el == null) {
                return null;
            }
            Map<String, String> map = new HashMap<>();
            map.put("name", name);
            map.put("identyfy", childText(el, "identyfy"));
            map.put("type", childText(el, "type"));
            map.put("content", childText(el, "content"));
            return map;
        } catch (Exception e) {
            throw new RuntimeException("读取 prompt.xml 失败: " + name, e);
        }
    }

    /**
     * 只取某个子标签的文本（自动 trim 掉 XML 里的换行缩进）
     */
    private String childText(Element parent, String tag) {
        NodeList list = parent.getElementsByTagName(tag);
        return list.getLength() > 0 ? list.item(0).getTextContent().trim() : "";
    }

}
