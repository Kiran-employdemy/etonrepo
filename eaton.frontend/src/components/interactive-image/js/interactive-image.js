if ($('.interactive-image-container').length > 0) {
	/**
	 * NOTE: disabling linting for legacy code from xcomfort.no. This should be refactored in the future and eslint enabled.
	 */
	/* eslint-disable */
	let image = $('.dataset').data('image');
    let image1 = $('.dataset').data('image1');
    let image2 = $('.dataset').data('image2');
    let image3 = $('.dataset').data('image3');
    let imagemob = $('.dataset').data('imagemob');
    let image1mob = $('.dataset').data('image1mob');
    let image2mob = $('.dataset').data('image2mob');
    let image3mob = $('.dataset').data('image3mob');
	let imageproperties = $('.interactive-image-container').attr('imageComponentData').replace(/[\r\n]\s*/g, '\n');
	let imagedata = JSON.parse(imageproperties);
	let redirectLink = $('.interactive-image-container').data('redirectLink');
	let summerJson;
	let winterJson;
	let randWinter;
	let randDesk;
	let randMob;
	let randSummer;
	let winterSeason;
	
	function summerRandImages() {
		let summerJson = [
			{
				desk: image,
				mob: imagemob
			},
			{
				desk: image1,
				mob: image1mob
			}
		];

		return summerJson;
	}

	function winterRandImages() {
		let winterJson = [
			{
				desk: image2,
				mob: image2mob
			},
			{
				desk: image3,
				mob: image3mob
			}
		];
		return winterJson;
	}

	jQuery(document).ready(function () {

		winterSeason = '1,2,3,10,11,12';
		winterSeason = winterSeason.split(',');
	});

	/** BEGIN SIMPLIFIED JS FROM eaton.min.js
	 * ui.apps/src/main/content/jcr_root/etc/designs/eaton/clientlib/components/beacon-clientLibraries/xComfortHomeClientlibs/scripts/eaton.min.js
	*/
	var HPA = function(a) {

		var pageData = imagedata;

			if(typeof pageData === 'undefined'){
				console.log("Please check the  xcomforthomepagehero configurations");
				return;
			}

			this.homeHeaderHidden = !1,
			this.animationInProgress = !1,
			this.lastUpdatedPosition = null,
			this.home_redirectInProgress = !1,
			this.home_scrollCounter = 0,
			this.home_loaderCounter = 0,
			this.needToRedirect = !1,
			this.mouseElement = ".hero-panel__scroll-link",
			this.desktopHeroImage = ".hero-panel__consumer .hero-panel__item-background.hero-panel__item-background--desktop",
			this.shortTextPanel = ".hero-panel__consumer .hero-panel__tile--short",
			this.moreTextPanel = ".hero-panel__consumer .hero-panel__tile--more",
			this.loader = ".loader",
			this.stickyHeader = ".header--sticky",
			this.fullHeader = ".header--full",
			this.ribbon = ".ribbon-link-list",
			this.cookieBanner = ".cookie-banner--show",
			this.loaderCircle = ".loader-circle",
			this.acceptedWebsites = [redirectLink],
			this.websiteNumber = Math.floor(Math.random() * this.acceptedWebsites.length),
			this.redirectionPage = this.acceptedWebsites[this.websiteNumber],
			this.scalableHeroImage = ".hero-panel__consumer .hero-panel__item-background",
			this.heroPanelItemDesktop = ".hero-panel__consumer .hero-panel__item-background--desktop",
			this.heroPanelItemMobile = ".hero-panel__consumer .hero-panel__item-background--mobile",
			this.sideOptions = ".meganav__list.meganav--visible",
			this.isAnimationInProgress = !1,
			this.sectionConfig = {
				"parallx-welcome": 0,
				"parallx-introduction": 50,
				"parallx-lifestyle": 300
			},
			this.timeline = ".parallax-nav",
			this.terminationConditionAutoAnimation = 0,
			this.autoStartSpeed = 100,
			this.enableDownArrowClick = !0,
			$.extend(this, a),
			$(this.selector).length && (this.init(), this.bindEvents())
		};
		HPA.prototype = {
			init: function() {
				$(window).width() <= 1024 ? self.deviceType = "mobile" : self.deviceType = "desktop", $(this.desktopHeroImage).show("slow"), this.updateLoaderPosition(), $(this.shortTextPanel).css("opacity", "1")
			},
			bindEvents: function() {
				var a = this;
				$(a.timeline + " a").unbind("click").bind("click", function() {
					if (a.enableDownArrowClick) {
						a.enableDownArrowClick = !1;
						var b = $(this).attr("class");
						if ($(a.timeline + " li").removeClass("active"), $(this).parent().addClass("active"), "undefined" !== b) {
							var c = b.split(" ");
							for (var d in c)
								if ("undefined" != typeof a.sectionConfig[c[d]]) {
									a.home_scrollCounter > a.sectionConfig[c[d]] ? a.terminationConditionAutoAnimation = "down" : a.terminationConditionAutoAnimation = "up", a.autoStart(a.sectionConfig[c[d]]);
									break
								}
						}
					}
				}), $(this.mouseElement).unbind("click").bind("click", function() {
					if (a.enableDownArrowClick) {
						a.enableDownArrowClick = !1;
						var b = $(a.timeline + " li.active a").parent().next().find("a").attr("class");
						"undefined" != typeof b && "parallx-lifestyle" == b && $(this).addClass("remove-animation"), "undefined" == typeof b && (b = $(a.timeline + " li.active a").attr("class")), $(a.timeline + " li").removeClass("active"), $("." + b).parent().addClass("active"), a.home_scrollCounter > a.sectionConfig[b] ? a.terminationConditionAutoAnimation = "down" : a.terminationConditionAutoAnimation = "up", a.autoStart(a.sectionConfig[b])
					}
				}), $(window).unbind("orientationchange").bind("orientationchange", function(a) {
					new HPA({
						selector: ".hero-panel__consumer",
						scrollDuration: 4,
						scrollDurationLoaderCounter: 4,
						autoStartSpeed: 50,
						touchScrollDuration: 3,
						touchScrollDurationLoaderCounter: 3
					})
				}), $(window).width() <= 1024 ? a.homePageMobileAnimation() : $(window).unbind("mousewheel DOMMouseScroll").bind("mousewheel DOMMouseScroll", function(b) {
					$(a.sideOptions).length || a._calculateScrollHomeAnimation("desktop", b), setTimeout(function() {
						//consumer.setMegaNavCloseIconHeight()
					}, 200)
				})
			},
			showWidgetHome: function(a, b) {
				switch (a) {
					case "short":
						this.isAnimationInProgress || ($(this.timeline + " li").removeClass("active"), $(this.timeline + " a.parallx-welcome").parent().addClass("active")), $(this.shortTextPanel).css("opacity", b / 100), $(this.moreTextPanel).css("opacity", 0);
						break;
					case "more":
						this.isAnimationInProgress || ($(this.timeline + " li").removeClass("active"), $(this.timeline + " a.parallx-introduction").parent().addClass("active")), $(this.shortTextPanel).css("opacity", 0), $(this.moreTextPanel).css("opacity", b / 100);
						break;
					case "none":
						this.isAnimationInProgress || ($(this.timeline + " li").removeClass("active"), $(this.timeline + " a.parallx-lifestyle").parent().addClass("active")), $(this.shortTextPanel).css("opacity", 0), $(this.moreTextPanel).css("opacity", 0)
				}
			},
			homeAnimationConfig: {
				"short": {
					min: 0,
					max: 25
				},
				more: {
					min: 26,
					max: 75
				},
				none: {
					min: 76,
					max: 100
				}
			},
			animateHeader: function(a) {
				var b = this;
				if ("show" == a) b.homeHeaderHidden && ($(b.loader).hide(), b.animationInProgress || (b.animationInProgress = !0, $(b.stickyHeader).slideUp(), $(b.ribbon + "," + b.fullHeader).animate({
					top: "0px",
					position: "relative"
				}, 200, function() {
					b.homeHeaderHidden = !1, b.animationInProgress = !1, $(b.stickyHeader).hide(), $(b.loader).show(), b.updateLoaderPosition("sticky")
				})));
				else if (!b.homeHeaderHidden) {
					var c = 0;
					$(b.ribbon).length && (c += $(b.ribbon).outerHeight()), $(b.loader).hide(), c += $(b.fullHeader).outerHeight(), b.animationInProgress || (b.animationInProgress = !0, $(b.ribbon + "," + b.fullHeader).animate({
						top: "-" + c + "px",
						position: "relative"
					}, 200, function() {
						b.homeHeaderHidden = !0, b.animationInProgress = !1, $(b.loader).show(), b.updateLoaderPosition("sticky"), $(b.stickyHeader).slideDown()
					}))
				}
			},
			updateLoaderPosition: function(a) {
				var b = 0;
				self.responsive_viewport > 991 && (b = $(this.ribbon).outerHeight());
				var c = b;
				$(this.cookieBanner).length && (c += 40), c += $(this.stickyHeader).hasClass("header--visible") ? $(this.stickyHeader).outerHeight() : $(this.fullHeader).outerHeight(), "undefined" != typeof a && "sticky" == a && (c = $(this.stickyHeader).outerHeight()), $(this.loader).css({
					"margin-top": c
				})
			},
			calculateOpacity: function(a, b, c, d) {
				var e = 0,
					f = 0,
					g = (a + b) / 2,
					h = 100 / (d + 1);
				return b >= c && c >= a ? c > g ? c > g && g + d >= c ? f = 100 : (e = b - c, f = e * h + d) : g >= c && c >= g - d ? f = 100 : (e = c - a, f = e * h + d) : f = 0, f
			},
			homePageAnimation: function(a, b) {
				for (var c in this.homeAnimationConfig) a >= this.homeAnimationConfig[c].min && a <= this.homeAnimationConfig[c].max && (14 > a ? this.showWidgetHome(c, 100) : this.showWidgetHome(c, this.calculateOpacity(this.homeAnimationConfig[c].min, this.homeAnimationConfig[c].max, a, 5)));
				"undefined" != typeof b && "mobile" != b && (a > 0 ? this.animateHeader("hide") : this.animateHeader("show"))
			},
			preventBounce: function(a, b) {
				var c = a.target;
				if (c != document.body) {
					var d = "";
					$(a.target).parents().andSelf().hasClass("scroll") && (d = "scroll", c = a.target.parentElement);
					var e = window.getComputedStyle(c),
						f = e.getPropertyValue("-webkit-overflow-scrolling"),
						g = parseInt(e.getPropertyValue("height"), 10),
						h = "auto" === f && ("auto" === d || "scroll" === d),
						i = c.scrollHeight > c.offsetHeight;
					if (h && i)
						if (1 == a.touches.length) {
							var j = a.touches ? a.touches[0].screenY : a.screenY,
								k = j >= b && 0 === c.scrollTop,
								l = b >= j && c.scrollHeight - c.scrollTop === g;
							(k || l) && a.preventDefault()
						} else a.preventDefault();
					else a.preventDefault()
				}
			},
			_homeScrollDown: function() {
				var a = this;
				a.home_scrollCounter += a.scrollDuration, a.home_scrollCounter > 75 && (a.home_loaderCounter += a.scrollDurationLoaderCounter)
			},
			_homeScrollUp: function() {
				var a = this;
				a.home_scrollCounter >= 0 ? a.home_scrollCounter -= a.scrollDuration : a.home_scrollCounter = 0, a.home_scrollCounter > 75 ? a.home_loaderCounter -= a.scrollDurationLoaderCounter : a.home_loaderCounter = 0
			},
			_calculateScrollHomeAnimation: function(a, b, c) {
				var d = this;
				if ("mobile" === a) {
					var e = b.originalEvent.changedTouches[0].clientY;
					c > e ? d.lastUpdatedPosition <= e ? (c = d.lastUpdatedPosition, d._homeScrollUp()) : d._homeScrollDown() : d.lastUpdatedPosition >= e ? d._homeScrollDown() : (c = d.lastUpdatedPosition, d._homeScrollUp()), d.lastUpdatedPosition = e
				} else b.originalEvent.wheelDelta > 0 || b.originalEvent.detail < 0 ? (d.needToRedirect = !1, d._homeScrollUp()) : d._homeScrollDown();
				return d.animation(), d.homePageAnimation(d.home_scrollCounter, a), c
			},
			animation: function() {
				var a = this,
					b = 1 + a.home_scrollCounter / 100;
				if (1 > b && (b = 1, a.home_scrollCounter = 0), b > 2 && (b = 2), b = "scale(" + b + ", " + b + ")", !a.home_redirectInProgress)
					if (a.home_loaderCounter > 0 && a.home_loaderCounter <= 100) {
						var c = 100 - a.home_loaderCounter;
						$(a.loader).css({
							top: "-" + c + "%"
						})
					} else a.home_loaderCounter >= 100 ? (a.home_redirectInProgress = !0, $(a.loaderCircle).show(), setTimeout(function() {
						if (typeof this.redirectionPage !=='undefined') {
							window.location = a.redirectionPage
						}
					}, 1e3)) : ($(a.loader).css({
						top: "-100%"
					}), $(a.scalableHeroImage).css("transform", b))
			},
			homePageMobileAnimation: function() {
				var a, b = this;
				this.scrollDuration = this.touchScrollDuration, this.scrollDurationLoaderCounter = this.touchScrollDurationLoaderCounter, $(b.heroPanelItemDesktop).hide(), $(b.heroPanelItemMobile).show(), $(document).bind("touchstart", function(b) {
					a = b.originalEvent.touches[0].clientY
				}), $(document).bind("touchmove", function(c) {
					$(b.sideOptions).length || (b.preventBounce(c, a), a = b._calculateScrollHomeAnimation("mobile", c, a))
				})
			},
			checkAutoTermination: function(a, b) {
				var c = this;
				"down" == c.terminationConditionAutoAnimation && c.home_scrollCounter <= a && (clearInterval(c.isAnimationInProgress), c.isAnimationInProgress = null, c.enableDownArrowClick = !0, "undefined" != typeof b && b()), "up" == c.terminationConditionAutoAnimation && c.home_scrollCounter >= a && (clearInterval(c.isAnimationInProgress), c.isAnimationInProgress = null, c.enableDownArrowClick = !0, "undefined" != typeof b && b())
			},
			autoStart: function(a, b) {
				var c = this;
				c.isAnimationInProgress = setInterval(function() {
					a > c.home_scrollCounter ? c._homeScrollDown() : c._homeScrollUp(), c.animation(), c.homePageAnimation(c.home_scrollCounter, c.deviceType), c.checkAutoTermination(a, b)
				}, c.autoStartSpeed)
			}
		}, $(document).ready(function() {
			new HPA({
				selector: ".hero-panel__consumer",
				scrollDuration: 4,
				scrollDurationLoaderCounter: 4,
				autoStartSpeed: 50,
				touchScrollDuration: 3,
				touchScrollDurationLoaderCounter: 3
			})
		}), $.fn.isOnScreen = function() {
			var a = $(window),
				b = {
					top: a.scrollTop(),
					left: a.scrollLeft()
				};
			b.right = b.left + a.width(), b.bottom = b.top + a.height();
			var c = this.offset();
			return c.right = c.left + this.outerWidth(), c.bottom = c.top + this.outerHeight(), !(b.right < c.left || b.left > c.right || b.bottom < c.top || b.top > c.bottom)
		};
	/* END SIMPLIFIED JS FROM eaton.min.js

	/* eslint-enable */
}
