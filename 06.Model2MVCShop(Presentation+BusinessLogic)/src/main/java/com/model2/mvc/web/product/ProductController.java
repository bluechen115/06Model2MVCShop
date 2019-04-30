package com.model2.mvc.web.product;

import java.net.URLDecoder;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Discount;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;

@Controller
public class ProductController {
	
	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;

	public ProductController() {
		System.out.println(this.getClass());
	}
	
	@Value("#{commonProperties['pageUnit']}")
	//@Value("#{commonProperties['pageUnit'] ?: 3}")
	int pageUnit;
	
	@Value("#{commonProperties['pageSize']}")
	//@Value("#{commonProperties['pageSize'] ?: 2}")
	int pageSize;
	
	@RequestMapping("/addProduct.do")
	public String addProduct(@ModelAttribute("product")Product product) throws Exception{
		System.out.println("/addProduct.do");
		
		productService.addProduct(product);
		
		return "forward:/product/successAddProduct.jsp";
	}
	
	@RequestMapping("/getProduct.do")
	public String getProduct(@RequestParam("prodNo") int prodNo,
								@RequestParam("menu") String menu,
								HttpServletRequest request,
								HttpServletResponse response,
								Model model) throws Exception{
		System.out.println("/getProduct.do");
		if(menu.equals("search")) {
			productService.plusViewCount(prodNo);
		}
		
		Map<String,Object> map=productService.getProduct(prodNo);
		Product product = (Product)map.get("product");
		Discount discount = (Discount)map.get("discount");
		
		//////////////Cookie//////////////////
		Cookie[] cookies = request.getCookies();
		Cookie cookie = null;

		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].getName().equals("history")) {
					cookie = cookies[i];
					break;
				}
			}

		}
		if (cookie != null) {
			response.addCookie(new Cookie("history", cookie.getValue() + "," + product.getProdNo()));
		} else {
			response.addCookie(new Cookie("history", String.valueOf(product.getProdNo())));
		}
		//////////////Cookie//////////////////
		
		model.addAttribute("product", product);
		model.addAttribute("discount", discount);
		
		if (menu != null) {
			if (menu.equals("search")) {
				return "forward:/product/getProduct.jsp";
			}
		}

		return "forward:/product/updateProductView.jsp";
	}
	
	@RequestMapping("/listProduct.do")
	public String getListProduct(HttpServletRequest request,
									@ModelAttribute("search") Search search,
									@ModelAttribute("page") Page page,
									@RequestParam("menu") String menu,
									Model model) throws Exception {
		System.out.println("/listProduct.do");
		
		page.setPageUnit(pageUnit);
		
		if(page.getPageSize()==0) {
		page.setPageSize(pageSize);
		}
		
		if(search.getSearchKeyword()!=null) {
			if(request.getMethod().equals("GET")) {
//				search.setSearchKeyword(CommonUtil.convertToKo(request.getParameter("searchKeyword")));
				search.setSearchKeyword(URLDecoder.decode(search.getSearchKeyword(), "EUC-KR"));
				System.out.println("GET방식으로 실행");
			}else {
				search.setSearchKeyword(search.getSearchKeyword());
				System.out.println("POST방식으로 실행, SearchKeyword :: "+search.getSearchKeyword());
			}
		}
		
		search.setPageSize(page.getPageSize());
		
		Map<String,Object> map=productService.getProductList(search);
		
		Page resultPage=new Page(search.getCurrentPage(),((Integer)map.get("totalCount")).intValue(), page.getPageUnit(), page.getPageSize());

		model.addAttribute("list", map.get("list"));
		model.addAttribute("discount", map.get("discount"));
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("search", search);

		return "forward:/product/listProduct.jsp";
		
	}
	
	@RequestMapping("/updateProduct.do")
	public String updateProduct(@ModelAttribute("product") Product product,
									Model model) throws Exception{
		
		System.out.println("/updateProduct.do");
		ProductService productService=new ProductServiceImpl();
		productService.updateProduct(product);
		
		Map<String,Object> map=productService.getProduct(product.getProdNo());
		product = (Product)map.get("product");
		
		return "forward:/product/getProduct.jsp";
	}
	
	
	

}
