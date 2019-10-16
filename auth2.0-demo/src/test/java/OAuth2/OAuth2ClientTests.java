package OAuth2;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.auth.Auth2BootDemoApplication;
import com.auth.dto.LoginUserDTO;
import com.auth.dto.UserDTO;
import com.auth.vo.ResponseVO;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * 基于Mock的单元测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Auth2BootDemoApplication.class }) // 指定启动类
@Slf4j
public class OAuth2ClientTests {

	private static final String API_PREFIX = "http://127.0.0.1:7777";
	@Autowired
	private RestTemplate restTemplate;

	/**
	 * setp1 登陆授权
	 * @throws Exception
	 */
	@Test
	public void loginTestCase() throws Exception {
		String api = API_PREFIX + "/auth/user/login";
		/**
		 * {
				"code":5001,
				"message":"账号密码不正确"
			}
		*/
		LoginUserDTO loginUserDTO = new LoginUserDTO("xiaohong", "123456");
		MultiValueMap<String, Object> paramMap = bean2MultiValueMap(loginUserDTO);
		HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<MultiValueMap<String, Object>>(paramMap, null);
		ResponseEntity<ResponseVO> responseEntity = restTemplate.exchange(api, HttpMethod.POST, httpEntity, ResponseVO.class);
		ResponseVO body = responseEntity.getBody();
		log.info(JSONObject.toJSONString(body,true));
	}
	
	
	/**参数转换
	 * @param obj
	 * @return
	 */
	private MultiValueMap<String, Object> bean2MultiValueMap(Object obj) {
		try {
			MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
			Class clazz = obj.getClass();
			Field[] fs = clazz.getDeclaredFields();
			for (Field field : fs) {
				String fName=field.getName();
				Method getMethod = clazz.getMethod("get"+fName.substring(0,1).toUpperCase()+fName.substring(1));
				Object fieldValue = getMethod.invoke(obj, null);
				paramMap.add(fName, fieldValue);
			}
			return paramMap;
		} catch (Exception e) {
			throw new RuntimeException("参数异常");
		}
	}
	
	/**
	 * 携带token访问资源
	 * @throws Exception
	 * 
	 * admin才可以访问
	 */
	@Test
	public void userAddTestCase() throws Exception {
		String api = API_PREFIX + "/auth/user";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer 1f16c31a-6d32-4ada-985c-225603716343");
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		UserDTO userDTO=new UserDTO();
    	userDTO.setAccount("xiaowang");
    	userDTO.setName("小王");
    	userDTO.setPassword("123456");
    	userDTO.setRoleId(1);
    	userDTO.setDescription("再添加一个新用户");
		HttpEntity<String> httpEntity = new HttpEntity<String>(JSONObject.toJSONString(userDTO), headers);
		/**
		 * {
			 	"code":0,
				"message":"成功"
			}
		 */
		
		/**
		 * {
				"code":1003,
				"message":"该用户权限不足以访问该资源接口"
			}
		 */
		ResponseEntity<ResponseVO> obj = restTemplate.exchange(api.toString(), HttpMethod.POST, httpEntity, ResponseVO.class);
		log.info(JSONObject.toJSONString(obj.getBody(),true));
	}
	
	/**
	 * 携带token访问资源
	 * @throws Exception
	 * 
	 * admin才可以访问
	 */
	@Test
	public void userDeleteTestCase() throws Exception {
		String api = API_PREFIX + "/auth/user/4";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer 084f8b76-ce7a-49b2-bf33-4fe6e8475c0c");
		HttpEntity<String> httpEntity = new HttpEntity<String>(null, headers);
		/**
		 * {
			 	"code":0,
				"message":"成功"
			}
		 */
		ResponseEntity<ResponseVO> obj = restTemplate.exchange(api.toString(), HttpMethod.DELETE, httpEntity, ResponseVO.class);
		log.info(JSONObject.toJSONString(obj.getBody(),true));
	}

	/**
	 * 携带token访问资源
	 * @throws Exception
	 * 
	 * admin和普通人员都可以访问
	 */
	@Test
	public void userListTestCase() throws Exception {
		String api = API_PREFIX + "/auth/user";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer 1f16c31a-6d32-4ada-985c-225603716343");
		HttpEntity<String> httpEntity = new HttpEntity<String>(null, headers);
		//{"code":1004,"message":"访问此资源需要完全的身份验证"}   token不传
		/**
		 * {
			    "code": 1001,
			    "message": "access_token无效"
			}
			token 已失效
		 */
		ResponseEntity<ResponseVO> obj = restTemplate.exchange(api.toString(), HttpMethod.GET, httpEntity, ResponseVO.class);
		log.info(JSONObject.toJSONString(obj.getBody(),true));
	}

	/**
	 * 认证注销
	 * @throws Exception
	 */
	@Test
	public void logoutTestCase() throws Exception {
		String api = API_PREFIX + "/auth/user/logout";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer 084f8b76-ce7a-49b2-bf33-4fe6e8475c0c");
		HttpEntity<String> httpEntity = new HttpEntity<String>(null, headers);
		ResponseEntity<ResponseVO> obj = restTemplate.exchange(api.toString(), HttpMethod.GET, httpEntity, ResponseVO.class);
		log.info(JSONObject.toJSONString(obj.getBody(),true));
	}

}
