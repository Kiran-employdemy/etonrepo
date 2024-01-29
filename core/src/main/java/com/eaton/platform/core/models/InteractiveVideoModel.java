package com.eaton.platform.core.models;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

/**
 * The Class InteractiveImageModel.
 */
@Model(adaptables = Resource.class)
public class InteractiveVideoModel   {
	
	@Inject @Optional
	private String  hostURL;

	@Inject @Optional
	private String videoExtension;
	
	@Inject @Optional
	private String room;

	@Inject @Optional
	private String carouselPrev;
	
	@Inject @Optional
	private String carouselNext;
	
	@Inject @Optional
	private String quitLabel;
	
	@Inject @Optional
	private String modaltext;

	@Inject @Optional
	private String redirectlink;

	@Inject @Optional
	private String title1;
	
	@Inject @Optional
	private String intro1;
	
	@Inject @Optional
	private String buttontext1;
	
	@Inject @Optional
	private String buttonlink1;
	
	@Inject @Optional
	private String title2;
	
	@Inject @Optional
	private String intro2;
	
	@Inject @Optional
	private String buttontext2;
	
	@Inject @Optional
	private String buttonlink2;
	
	@Inject @Optional
	private String title3;
	
	@Inject @Optional
	private String intro3;
	
	@Inject @Optional
	private String buttontext3;
	
	@Inject @Optional
	private String buttonlink3;
	
	@Inject @Optional
	private String title4;
	
	@Inject @Optional
	private String intro4;
	
	@Inject @Optional
	private String buttontext4;
	
	@Inject @Optional
	private String buttonlink4;
	
	@Inject @Optional
	private String title5;
	
	@Inject @Optional
	private String intro5;
	
	@Inject @Optional
	private String buttontext5;
	
	@Inject @Optional
	private String buttonlink5;
	
	@Inject @Optional
	private String title6;
	
	@Inject @Optional
	private String intro6;
	
	@Inject @Optional
	private String buttontext6;
	
	@Inject @Optional
	private String buttonlink6;
		

	public String getHostURL() {
		return hostURL;
	}

	public String getVideoExtension() {
		return videoExtension;
	}

	public String getRoom() {
		return room;
	}

	public String getCarouselPrev() {
		return carouselPrev;
	}

	public String getCarouselNext() {
		return carouselNext;
	}

	public String getQuitLabel() {
		return quitLabel;
	}
	
	public String getModaltext() {
		return modaltext;
	}
	
	public String getRedirectlink() {
		return redirectlink;
	}
	
	public String getTitle1() {
		return title1;
	}
	
	public String getIntro1() {
		return intro1;
	}
	
	public String getButtontext1() {
		return buttontext1;
	}
	
	public String getButtonlink1() {
		return buttonlink1;
	}
	
	public String getTitle2() {
		return title2;
	}
	
	public String getIntro2() {
		return intro2;
	}
	
	public String getButtontext2() {
		return buttontext2;
	}
	
	public String getButtonlink2() {
		return buttonlink2;
	}
	
	public String getTitle3() {
		return title3;
	}
	
	public String getIntro3() {
		return intro3;
	}
	
	public String getButtontext3() {
		return buttontext3;
	}
	
	public String getButtonlink3() {
		return buttonlink3;
	}
	
	public String getTitle4() {
		return title4;
	}
	
	public String getIntro4() {
		return intro4;
	}
	
	public String getButtontext4() {
		return buttontext4;
	}
	
	public String getButtonlink4() {
		return buttonlink4;
	}
	
	public String getTitle5() {
		return title5;
	}
	
	public String getIntro5() {
		return intro5;
	}
	
	public String getButtontext5() {
		return buttontext5;
	}
	
	public String getButtonlink5() {
		return buttonlink5;
	}
	
	public String getTitle6() {
		return title6;
	}
	
	public String getIntro6() {
		return intro6;
	}
	
	public String getButtontext6() {
		return buttontext6;
	}
	
	public String getButtonlink6() {
		return buttonlink6;
	}
}