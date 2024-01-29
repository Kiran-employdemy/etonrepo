package com.eaton.platform.core.models;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

/**
 * The Class InteractiveImageModel.
 */
@Model(adaptables = Resource.class)
public class InteractiveImageModel   {
	
	@Inject @Optional
	private String  title;

	@Inject @Optional
	private String title1;
	
	@Inject @Optional
	private String desc;

	@Inject @Optional
	private String radioButtonText1;
	
	@Inject @Optional
	private String radioButtonText2;
	
	@Inject @Optional
	private String radioButtonText3;
	
	@Inject @Optional
	private String imageOnright;

	@Inject @Optional
	private String imageOnleft;

	@Inject @Optional
	private String redirectLink;
	
	@Inject @Optional
	private String image;
	
	@Inject @Optional
	private String image1;
	
	@Inject @Optional
	private String imagemob;
	
	@Inject @Optional
	private String image1mob;
	
	@Inject @Optional
	private String image2;
	
	@Inject @Optional
	private String image3;
	
	@Inject @Optional
	private String image2mob;
	
	@Inject @Optional
	private String image3mob;
		

	public String getTitle() {
		return title;
	}

	public String getTitle1() {
		return title1;
	}

	public String getDesc() {
		return desc;
	}

	public String getRadioButtonText1() {
		return radioButtonText1;
	}

	public String getRadioButtonText2() {
		return radioButtonText2;
	}

	public String getRadioButtonText3() {
		return radioButtonText3;
	}
	
	public String getImageOnright() {
		return imageOnright;
	}
	
	public String getImageOnleft() {
		return imageOnleft;
	}
	
	public String getRedirectLink() {
		return redirectLink;
	}
	
	public String getImage() {
		return image;
	}
	
	public String getImage1() {
		return image1;
	}
	
	public String getImagemob() {
		return imagemob;
	}
	
	public String getImage1mob() {
		return image1mob;
	}
	
	public String getImage2() {
		return image2;
	}
	
	public String getImage3() {
		return image3;
	}
	
	public String getImage2mob() {
		return image2mob;
	}
	
	public String getImage3mob() {
		return image3mob;
	}
}