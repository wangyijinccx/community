package com.ipeaksoft.moneyday.api.controller;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.ipeaksoft.moneyday.api.util.MD5Util;
import com.ipeaksoft.moneyday.core.entity.CommGame;
import com.ipeaksoft.moneyday.core.service.CommGameService;
import com.ipeaksoft.moneyday.core.util.strUtil;

@Controller
@RequestMapping(value = "/game")
public class CommGameController extends BaseController {

	@Autowired
	CommGameService commGameService;

	/**
	 * 添加游戏
	 * 
	 * @param webinarId
	 * @param token
	 * @param pos
	 * @param response
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@ResponseBody
	@RequestMapping("add")
	public Object add(HttpServletRequest request, HttpServletResponse response) {
		JSONObject result = new JSONObject();
		JSONObject json = new JSONObject();
		try {
			Map<String, String[]> maps = request.getParameterMap();
			String js = strUtil.map2JsonString(maps);
			json = JSONObject.parseObject(js);
			logger.info("comm_gameadd:{}", json.toString());
			if (!PLAT_ID.equals(json.getString("plat_id"))) {
				result.put("code", 401);
				result.put("fun", "/game/add");
				result.put("time", new Date());
				result.put("info", json);
				sdklogger.info("ERROR:{}", result.toString());
				return result;
			}
			String sign = MD5Util.md5("plat_id=" + URLEncoder.encode(PLAT_ID)
					+ "&app_id=" + URLEncoder.encode(json.getString("app_id"))
					+ "&timestamp="
					+ URLEncoder.encode(json.getString("timestamp"))
					+ "&gamename="
					+ URLEncoder.encode(json.getString("gamename"))
					+ "&classify="
					+ URLEncoder.encode(json.getString("classify"))
					+ "&gameflag="
					+ URLEncoder.encode(json.getString("gameflag"))
					+ "&creat_time="
					+ URLEncoder.encode(json.getString("creat_time"))
					+ "&status=" + URLEncoder.encode(json.getString("status"))
					+ "&pinyin=" + URLEncoder.encode(json.getString("pinyin"))
					+ "&initial="
					+ URLEncoder.encode(json.getString("initial"))
					+ "&version="
					+ URLEncoder.encode(json.getString("version"))
					+ "&PLAT_SECURE_KEY=" + URLEncoder.encode(PLAT_SECURE_KEY));
			String reqSign = json.getString("sign");
			if (!sign.equals(reqSign)) {
				result.put("code", 404);
				result.put("fun", "/game/add");
				result.put("time", new Date());
				result.put("info", json);
				sdklogger.info("ERROR:{}", result.toString());
				return result;
			}
			CommGame commGame = new CommGame();
			commGame.setPlatId(json.getInteger("plat_id"));
			commGame.setGameId(json.getInteger("app_id"));
			commGame.setName(json.getString("gamename"));
			commGame.setGameflag(json.getString("gameflag"));
			commGame.setInitial(json.getString("initial"));
			commGame.setIcon(json.getString("icon"));
			commGame.setStatus(json.getByte("status"));
			commGame.setClassify(json.getByte("classify"));
			commGame.setCreateTime(json.getLong("creat_time"));
			commGame.setUpdateTime(json.getLong("creat_time"));
			commGame.setListorder(0);
			commGame.setTargetCnt(null == json.getInteger("target_cnt") ? 7
					: json.getInteger("target_cnt"));
			commGame.setTargetLevel(null == json.getInteger("target_level") ? 140
					: json.getInteger("target_level"));
			commGame.setParentId(null == json.getInteger("parent_id") ? 0
					: json.getInteger("parent_id"));
			if (commGameService.insertSelective(commGame) < 1) {
				result.put("code", 1000);
				result.put("fun", "/game/add");
				result.put("time", new Date());
				result.put("info", json);
				sdklogger.info("ERROR:{}", result.toString());
				return result;
			}

		} catch (Exception e) {
			result.put("code", 1000);
			result.put("fun", "/game/add");
			result.put("time", new Date());
			result.put("info", json);
			sdklogger.info("ERROR:{}", result.toString());
			return result;
		}
		result.put("code", 200);
		return result;
	}

	/**
	 * 游戏修改接口
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@ResponseBody
	@RequestMapping("update")
	public Object update(HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject result = new JSONObject();
		Map<String, String> map = new HashMap<String, String>();
		try {
			Map<String, String[]> maps = request.getParameterMap();
			map = strUtil.getMap(maps);
			JSONObject jsonUpInfo = JSONObject.parseObject(map.get("upinfo"));
			logger.info("comm_gameupdate:{}", map.toString());
			if (!PLAT_ID.equals(map.get("plat_id"))) {
				result.put("code", 401);
				result.put("fun", "/game/update");
				result.put("time", new Date());
				result.put("info", map.toString());
				sdklogger.info("ERROR:{}", result.toString());
				return result;
			}

			String sign = MD5Util.md5("plat_id=" + URLEncoder.encode(PLAT_ID)
					+ "&app_id=" + URLEncoder.encode(map.get("app_id"))
					+ "&timestamp=" + URLEncoder.encode(map.get("timestamp"))
					+ "&upinfo=" + URLEncoder.encode(jsonUpInfo.toString())
					+ "&PLAT_SECURE_KEY=" + URLEncoder.encode(PLAT_SECURE_KEY));
			String reqSign = map.get("sign");
			if (!sign.equals(reqSign)) {
				result.put("code", 404);
				result.put("fun", "/game/update");
				result.put("time", new Date());
				result.put("info", map.toString());
				sdklogger.info("ERROR:{}", result.toString());
				return result;
			}
			CommGame commGame = new CommGame();
			commGame.setPlatId(Integer.parseInt(map.get("plat_id")));
			commGame.setGameId(Integer.parseInt(map.get("app_id")));
			commGame.setName(jsonUpInfo.getString("gamename"));
			commGame.setGameflag(jsonUpInfo.getString("gameflag"));
			commGame.setInitial(jsonUpInfo.getString("initial"));
			commGame.setIcon(jsonUpInfo.getString("icon"));
			commGame.setStatus(jsonUpInfo.getByte("status"));
			commGame.setClassify(jsonUpInfo.getByte("classify"));
			commGame.setUpdateTime(jsonUpInfo.getLong("update_time"));
			commGame.setListorder(0);
			if (null != jsonUpInfo.getInteger("target_cnt")) {
				commGame.setTargetCnt(jsonUpInfo.getInteger("target_cnt"));
			}
			if (null != jsonUpInfo.getInteger("target_level")) {
				commGame.setTargetLevel(jsonUpInfo.getInteger("target_level"));
			}
			if (null != jsonUpInfo.getInteger("parent_id")) {
				commGame.setParentId(jsonUpInfo.getInteger("parent_id"));
			}
			// 单条修改，为了记录某条出错数据 而且也没几个游戏就不做批量了
			if (commGameService.updateByGameid(commGame) < 1) {
				result.put("code", 1000);
				result.put("fun", "/game/update");
				result.put("time", new Date());
				result.put("info", map.toString());
				result.put("updateerro", jsonUpInfo);
				sdklogger.info("ERROR:{}", result.toString());
				// return result;
			}
		} catch (Exception e) {
			result.put("code", 1000);
			result.put("fun", "/game/update");
			result.put("time", new Date());
			result.put("info", map.toString());
			sdklogger.info("ERROR:{}", result.toString());
			return result;
		}
		result.put("code", 200);
		return result;
	}

	/**
	 * 游戏删除接口--修改上下线时间
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@ResponseBody
	@RequestMapping("delete")
	public Object delete(HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject result = new JSONObject();
		JSONObject json = new JSONObject();
		try {
			Map<String, String[]> maps = request.getParameterMap();
			String js = strUtil.map2JsonString(maps);
			json = JSONObject.parseObject(js);
			logger.info("comm_gamedelete:{}", json.toString());
			if (!PLAT_ID.equals(json.getString("plat_id"))) {
				result.put("code", 401);
				result.put("fun", "/game/delete");
				result.put("time", new Date());
				result.put("info", json);
				sdklogger.info("ERROR:{}", result.toString());
				return result;
			}
			String sign = MD5Util.md5("plat_id=" + URLEncoder.encode(PLAT_ID)
					+ "&app_id=" + URLEncoder.encode(json.getString("app_id"))
					+ "&timestamp="
					+ URLEncoder.encode(json.getString("timestamp"))
					+ "&delete_time="
					+ URLEncoder.encode(json.getString("delete_time"))
					+ "&PLAT_SECURE_KEY=" + URLEncoder.encode(PLAT_SECURE_KEY));
			String reqSign = json.getString("sign");
			if (!sign.equals(reqSign)) {
				result.put("code", 404);
				result.put("fun", "/game/delete");
				result.put("time", new Date());
				result.put("info", json);
				sdklogger.info("ERROR:{}", result.toString());
				return result;
			}
			CommGame commGame = new CommGame();
			commGame.setGameId(json.getInteger("app_id"));
			commGame.setStatus((byte) 3);
			commGame.setUpdateTime(json.getLong("delete_time"));
			if (commGameService.updateByGameid(commGame) < 1) {
				result.put("code", 1000);
				result.put("fun", "/game/delete");
				result.put("time", new Date());
				result.put("info", json);
				sdklogger.info("ERROR:{}", result.toString());
				return result;
			}

		} catch (Exception e) {
			result.put("code", 1000);
			result.put("fun", "/game/delete");
			result.put("time", new Date());
			result.put("info", json);
			sdklogger.info("ERROR:{}", result.toString());
			return result;
		}
		result.put("code", 200);
		return result;
	}
    
	/**
	 * 删除还原游戏
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@ResponseBody
	@RequestMapping("restore")
	public Object restore(HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject result = new JSONObject();
		JSONObject json = new JSONObject();
		try {
			Map<String, String[]> maps = request.getParameterMap();
			String js = strUtil.map2JsonString(maps);
			json = JSONObject.parseObject(js);
			logger.info("comm_gamerestore:{}", json.toString());
			if (!PLAT_ID.equals(json.getString("plat_id"))) {
				result.put("code", 401);
				result.put("fun", "/game/restore");
				result.put("time", new Date());
				result.put("info", json);
				sdklogger.info("ERROR:{}", result.toString());
				return result;
			}
			String sign = MD5Util.md5("plat_id=" + URLEncoder.encode(PLAT_ID)
					+ "&app_id=" + URLEncoder.encode(json.getString("app_id"))
					+ "&timestamp="
					+ URLEncoder.encode(json.getString("timestamp"))
					+ "&restore_time="
					+ URLEncoder.encode(json.getString("restore_time"))
					+ "&PLAT_SECURE_KEY=" + URLEncoder.encode(PLAT_SECURE_KEY));
			String reqSign = json.getString("sign");
			if (!sign.equals(reqSign)) {
				result.put("code", 404);
				result.put("fun", "/game/restore");
				result.put("time", new Date());
				result.put("info", json);
				sdklogger.info("ERROR:{}", result.toString());
				return result;
			}
			CommGame commGame = new CommGame();
			commGame.setGameId(json.getInteger("app_id"));
			commGame.setStatus((byte) 1);
			commGame.setUpdateTime(json.getLong("restore_time"));
			if (commGameService.updateByGameid(commGame) < 1) {
				result.put("code", 1000);
				result.put("fun", "/game/restore");
				result.put("time", new Date());
				result.put("info", json);
				sdklogger.info("ERROR:{}", result.toString());
				return result;
			}
		} catch (Exception e) {
			result.put("code", 1000);
			result.put("fun", "/game/restore");
			result.put("time", new Date());
			result.put("info", json);
			sdklogger.info("ERROR:{}", result.toString());
			return result;
		}
		result.put("code", 200);
		return result;
	}
    
	/**
	 * 获取游戏列表
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getgamelist")
	public Object getGameList(HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject result = new JSONObject();
		List<Map<String, Object>> games = commGameService.getGameList();
		result.put("result", 1);
		result.put("gameList", games);
		result.put("msg", "获取游戏列表成功");
		return result;
	}

}
