package jsug.app.goods;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jsug.domain.model.Cart;
import jsug.domain.model.Category;
import jsug.domain.model.Goods;
import jsug.domain.model.OrderLine;
import jsug.domain.service.category.CategoryService;
import jsug.domain.service.goods.GoodsService;

@Controller
public class GoodsController {
	@Autowired
	GoodsService goodsService;
	@Autowired
	CategoryService categoryService;
	@Autowired
	Cart cart;
	
	@ModelAttribute("categories")
	List<Category> getCategories() {
		return categoryService.findAll();
	}
	
	@ModelAttribute
	AddToCartForm addToCartForm() {
		return new AddToCartForm();
	}
	
	@RequestMapping(value = "/")
	String showGoods(@RequestParam(defaultValue = "1") Integer categoryId, 
			@PageableDefault Pageable pageable, Model model) {
		Page<Goods> page = goodsService.findByCategoryId(categoryId, pageable);
		model.addAttribute("page", page);
		model.addAttribute("categoryId", categoryId);
		return "goods/showGoods";
	}
	
	@RequestMapping(value = "/addToCart", method = RequestMethod.POST)
	String addToCart(@Validated AddToCartForm form, BindingResult result,
            @PageableDefault Pageable pageable, Model model) {
		if (result.hasErrors()) {
			return showGoods(form.getCategoryId(), pageable, model);
		}
		Goods goods = goodsService.findOne(form.getGoodsId());
		cart.add(OrderLine.builder()
				.goods(goods)
				.quantity(form.getQuantity())
				.build());
		return "redirect:/cart";
	}
}
