package ai.s_ai.tools;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@ConfigurationProperties("config.gaode")
@Component
@Data
@Slf4j
public class GaodeTools {
    private String apiKey;
    private String secretKey;
    private String amapIpUrl;
    private String amapWeather;
    private String amapDriving;
    private String amapWalking;
    private String amapGeocode;


    @Tool(description = "获取用户定位或位置时使用，未传入ip时获取ip")
    public String getUserIp(@ToolParam(description = "用户ip，获取用户所在位置，未传入则获取用户ip") String ip) throws IOException {
        String urlStr = amapIpUrl + "?output=xml&key=" + apiKey;
        if (ip != null && !ip.isEmpty()) {
            urlStr += "&ip=" + ip;
        }
        log.info("ai调用了工具：{}", urlStr);
        return getString(urlStr);

    }

    @Tool(description = "获取用户所在位置的天气情况，需要先获取Ip信息，当用户需要天气信息、用户提到未来/过去的穿着、前往某地、进行某些室外活动时调用，给出穿着、装备建议。")
    public String getIpWeather(
            @ToolParam(description = "城市编码，输入城市的adcode，adcode信息可参考城市编码表，必填") String adcode,
            @ToolParam(description = "气象类型，可选值：base/all，base:返回实况天气，all:返回预报天气，可选"
            ) String extensions) throws IOException {
        String urlStr = amapWeather + "?output=xml&key=" + apiKey + "&city=" + adcode + "&extensions=" + extensions;
        return getString(urlStr);
    }

    @Tool(description =
            "获取目标地点的地理编码，获取地点所在国家、省份、城市、区县、街道、详细地址、经纬度（坐标）、区域编码、城市编码,地理编码：将详细的结构化地址转换为高德经纬度坐标。" +
                    "且支持对地标性名胜景区、建筑物名称解析为高德经纬度坐标。 结构化地址举例：北京市朝阳区阜通东大街6号转换后经纬度：116.480881,39.989410  地标性建筑举例：天安门转换后经纬度：116.397499,39.908722\n" +
                    "逆地理编码：将经纬度转换为详细结构化的地址，且返回附近周边的 POI、AOI 信息。 例如：116.480881,39.989410 转换地址描述后：北京市朝阳区阜通东大街6号")
    public String getGeocode(@ToolParam(description = "传入目标地点（address）的地址（可指定查询城市（city）），获取目标地点的地理编码") String address, String city) throws IOException {
        String urlStr = amapGeocode + "?output=xml&key=" + apiKey + "&address=" + address;
        if (city != null && !city.isEmpty()) {
            urlStr += "&city=" + city;
        }
        return getString(urlStr);
    }

    @Tool(description = "路径规划（步行、公交、驾车），传入起点和终点的经纬度，返回路径信息，先查询起点和终点的经纬度，再查询路径信息")
    public String walking(
            @ToolParam(description = "出发点，规则：lon,lat（经度，纬度），用英文逗号,分割，如117.500244,40.417801，经纬度小数点不超过6位，必填") String origin,
            @ToolParam(description = "目的地，规则：lon,lat（经度，纬度），用英文逗号,分割，如117.500244,40.417801，经纬度小数点不超过6位，必填") String destination,
            @ToolParam(description = "起点POI ID，起点为POI时建议填充此值，可提升路线规划准确性，可选，无默认") String origin_id,
            @ToolParam(description = "目的地POI ID，目的地为POI时建议填充此值，可提升路线规划准确性，可选，无默认") String destination_id
    ) throws IOException {
        String urlStr = amapWalking + "?output=xml&key=" + apiKey + "&origin=" + origin + "&destination=" + destination;
        if (origin_id != null && !origin_id.isEmpty()) {
            urlStr += "&origin_id=" + origin_id;
        }
        if (destination_id != null && !destination_id.isEmpty()) {
            urlStr += "&destination_id=" + destination_id;
        }
        return getString(urlStr);
    }


    @Tool(description = "路径规划（以驾车、公交、步行、骑行、电动车），传入起点和终点的经纬度，返回路径信息，先查询起点和终点的经纬度，再查询路径信息")
    public String driving(
            @ToolParam(description = "起点经纬度，经度在前、纬度在后，用英文逗号,分割，小数点后不得超过6位，如116.481028,39.989643") String origin,
            @ToolParam(description = "终点经纬度，格式同起点") String destination,
            @ToolParam(description = "起点POI ID，起点为POI时建议填充此值，可提升路线规划准确性，可选，无默认") String origin_id,
            @ToolParam(description = "目的地POI ID，目的地为POI时建议填充此值，可提升路线规划准确性，可选，无默认") String destination_id,
            @ToolParam(description = "驾车算路策略，可选，默认32。"
                    + "0：速度优先（只返回一条路线，不一定距离最短）；"
                    + "1：费用优先（不走收费路段且耗时最少）；"
                    + "2：常规最快（综合距离/耗时）；"
                    + "32：默认，高德推荐；"
                    + "33：躲避拥堵；34：高速优先；35：不走高速；"
                    + "36：少收费；37：大路优先；38：速度最快；"
                    + "39：躲避拥堵+高速优先；40：躲避拥堵+不走高速；"
                    + "41：躲避拥堵+少收费；42：少收费+不走高速；"
                    + "43：躲避拥堵+少收费+不走高速；"
                    + "44：躲避拥堵+大路优先；45：躲避拥堵+速度最快") Integer strategy,
            @ToolParam(description = "途经点坐标串，多个按顺序用英文分号;分隔，默认支持1个有序途径点，最大16个途经点，可选，默认无") String waypoints,
            @ToolParam(description = "避让区域坐标串，多个区域用英文竖线|分隔，每区域最多16个顶点（四边形4个、五边形5个坐标点），最大支持32个区域，每个区域不能超过81平方公里否则失效，可选，默认无") String avoidpolygons,
            @ToolParam(description = "车牌号码，如京AHA322，支持6位传统车牌和7位新能源车牌，用于判断限行，可选，默认无") String plate,
            @ToolParam(description = "车辆类型，可选，默认0。0：普通燃油汽车；1：纯电动汽车；2：插电式混动汽车") String cartype,
            @ToolParam(description = "是否使用轮渡，可选，默认0。0：使用渡轮；1：不使用渡轮") Integer ferry
    ) throws IOException {
        String urlStr = amapDriving + "?output=xml&key=" + apiKey + "&origin=" + origin + "&destination=" + destination;
        if (origin_id != null && !origin_id.isEmpty()) urlStr += "&origin_id=" + origin_id;
        if (destination_id != null && !destination_id.isEmpty()) urlStr += "&destination_id=" + destination_id;
        if (strategy != null) urlStr += "&strategy=" + strategy;
        if (waypoints != null && !waypoints.isEmpty()) urlStr += "&waypoints=" + waypoints;
        if (avoidpolygons != null && !avoidpolygons.isEmpty()) urlStr += "&avoidpolygons=" + avoidpolygons;
        if (plate != null && !plate.isEmpty()) urlStr += "&plate=" + plate;
        if (cartype != null && !cartype.isEmpty()) urlStr += "&cartype=" + cartype;
        if (ferry != null) urlStr += "&ferry=" + ferry;
        return getString(urlStr);
    }

    @NonNull
    private String getString(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("GET");
        http.setConnectTimeout(5000);
        http.setReadTimeout(5000);
        int code = http.getResponseCode();
        StringBuilder sb = new StringBuilder();
        if (code == 200) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(http.getInputStream(), "UTF-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            bufferedReader.close();
        }
        http.disconnect();
        return sb.toString();
    }

}