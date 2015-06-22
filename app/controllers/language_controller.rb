class LanguageController < ApplicationController
  before_action :authenticate_user!
  include LanguageHelper
  def index
    @language_obj = LanguageList::LanguageInfo.find(params[:iso_language])
    get_keyboards_for_language(params[:iso_language])
  end

  def update_region_name
    keyboard_id = params["id"]
    region_code = params["region"]
    call_type = params["type"]
    #@keyboard_details = KeyPosition.find(id)
    #@key_details.update_attributes(:column_index => index)
    @keyboard_details = Keyboard.find(keyboard_id)
    previous_region = Keyboard.find(keyboard_id).iso_region
    @keyboard_details.update_attributes(:iso_region => region_code )
    language = @keyboard_details.iso_language
    if call_type == 'lang'
      get_keyboards_for_language(language)
      render :partial => 'language/key_with_diff_region', :locals => {:keyboards_for_language_iso => @keyboards_for_language_iso,:all_regions => @all_regions}
    elsif call_type == 'reg'
      get_keyboards_for_region(previous_region)
      render :partial => 'language/key_with_diff_lang', :locals => {:keyboards_for_region_iso => @keyboards_for_region_iso,:all_regions => @all_regions}
    end
  end

  def get_reg
    #KeyboardLanguages.order(:lc).map{|p| "#{p.lc}-#{p.ln}"}  
    @regions = KeyboardLanguages.order(:lc).where("lc like ? OR ln like ?", "%#{params[:term]}%", "%#{params[:term]}%").map{|p| "#{p.lc}-#{p.ln}"}
    response = {:values => params[:term] }
    #render :json => response
    #render plain: "OK"
    #respond_to do |format|
   #   format.json {render :json => response}
	flash[:notice] = 'Your message!'
    #end
    
    #respond_to do |format|  
    #   format.html 
    #   format.json { render :json => @regions.to_json }
    #end
  end

  def get_reg_name
    language = params["lang"]
    @region = KeyboardLanguages.where(lc: language).first
    region_name = @region.lr
    region_with_code = 'no_region'
    if !region_name.blank?
        @lang = KeyboardCountry.find(LangRegions.where(keyboardLanguages_id: @region.id).first.keyboardCountry_id)
    	region_with_code = @lang.cc+'-'+region_name
    end
    response = {:region_name => region_with_code}
    respond_to do |format|
      format.json {render :json => response}
    end
  end

  def update_unicode_url
    language_id = params[:id]
    unicode_url = params[:url]
    @language = KeyboardLanguages.find(language_id)
    @language.update_attributes(:search_unicode_url => unicode_url )
    respond_to do |format|
      format.json {render :json => true}
    end
  end
end

