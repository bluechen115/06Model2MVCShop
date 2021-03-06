package com.model2.mvc.web.product;

import java.net.URLDecoder;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Discount;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;
import com.model2.mvc.service.purchase.PurchaseService;

@Controller
public class ProductController {
	
	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;
	
	@Autowired
	@Qualifier("purchaseServiceImpl")
	private PurchaseService purchaseService;

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
	public ModelAndView addProduct(@ModelAttribute("product")Product product) throws Exception{
		System.out.println("/addProduct.do");
		
		productService.addProduct(product);
		
		ModelAndView modelAndView=new ModelAndView();
		modelAndView.setViewName("forward:/product/successAddProduct.jsp");
		
		return modelAndView;
	}
	
	@RequestMapping("/getProduct.do")
	public ModelAndView getProduct(@RequestParam("prodNo") int prodNo,
								@RequestParam("menu") String menu,
								HttpServletRequest request,
								HttpServletResponse response,
								HttpSession session
								) throws Exception{
		System.out.println("/getProduct.do");
		
		User user=(User)session.getAttribute("user");
		
		if(menu.equals("search")) {
			productService.plusViewCount(prodNo);
		}
		
		Map<String,Object> map=productService.getProduct(prodNo);
		Product product = (Product)map.get("product");
		Discount discount = (Discount)map.get("discount");
		
		int purchaseCount = purchaseService.getCountPurchase(user.getUserId());
		int price=product.getPrice();
		if(product.getProdNo()==discount.getDiscountProd()) {
			price=(int)(product.getPrice()*0.75);
		}
		if(purchaseCount % 4 == 0) {
			price=(int)(price*0.9);
		}
		
		product.setResultPrice(price);
		
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
		String viewName="";
		if (menu != null) {
			if (menu.equals("search")) {
				viewName= "forward:/product/getProduct.jsp";
			}else {
				viewName="forward:/product/updateProductView.jsp";
			}
		}

		ModelAndView modelAndView=new ModelAndView();
		modelAndView.setViewName(viewName);
		modelAndView.addObject("product", product);
		modelAndView.addObject("discount", discount);
		modelAndView.addObject("purchaseCount", purchaseCount);
		
		return modelAndView;
	}
	
	@RequestMapping("/listProduct.do")
	public ModelAndView getListProduct(HttpServletRequest request,
									@ModelAttribute("search") Search search,
									@ModelAttribute("page") Page page,
									@RequestParam("menu") String menu
									) throws Exception {
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

		ModelAndView modelAndView=new ModelAndView();
		modelAndView.addObject("list", map.get("list"));
		modelAndView.addObject("discount", map.get("discount"));
		modelAndView.addObject("resultPage", resultPage);
		modelAndView.addObject("search", search);
		modelAndView.setViewName("forward:/product/listProduct.jsp");
		
		return modelAndView;
		
	}
	
	@RequestMapping("/updateProduct.do")
	public ModelAndView updateProduct(@ModelAttribute("product") Product product) throws Exception{
		
		System.out.println("/updateProduct.do");
		ProductService productService=new ProductServiceImpl();
		productService.updateProduct(product);
		
		Map<String,Object> map=productService.getProduct(product.getProdNo());
		product = (Product)map.get("product");
		
		ModelAndView modelAndView=new ModelAndView();
		modelAndView.setViewName("forward:/product/getProduct.jsp");
		
		return modelAndView;
	}
	
	
	

}
