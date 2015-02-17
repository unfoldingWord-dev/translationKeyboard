class LanguageController < ApplicationController
  before_action :authenticate_user!
  def index
    @language_obj = LanguageList::LanguageInfo.find(params[:iso_language])
    @keyboards_for_language_iso = Keyboard.where(iso_language: params[:iso_language])
    sql = 'SELECT kc.cc,kl.lr FROM keyboard_countries kc, keyboard_languages kl, (SELECT DISTINCT ON("keyboardCountry_id") "keyboardCountry_id", "keyboardLanguages_id" FROM lang_regions ORDER BY "keyboardCountry_id") lg WHERE kc.id=lg."keyboardCountry_id" AND kl.id=lg."keyboardLanguages_id";'
    @all_regions = ActiveRecord::Base.connection.execute(sql)
  end

  def update_region_name
    keyboard_id = params["id"]
    region_code = params["region"]
    #@keyboard_details = KeyPosition.find(id)
    #@key_details.update_attributes(:column_index => index)
    @keyboard_details = Keyboard.find(keyboard_id)
    @keyboard_details.update_attributes(:iso_region => region_code )
    language = @keyboard_details.iso_language
    @keyboards_for_language_iso = Keyboard.where(iso_language: language)
    sql = 'SELECT kc.cc,kl.lr FROM keyboard_countries kc, keyboard_languages kl, (SELECT DISTINCT ON("keyboardCountry_id") "keyboardCountry_id", "keyboardLanguages_id" FROM lang_regions ORDER BY "keyboardCountry_id") lg WHERE kc.id=lg."keyboardCountry_id" AND kl.id=lg."keyboardLanguages_id";'
    @all_regions = ActiveRecord::Base.connection.execute(sql)
    render :partial => 'language/key_with_diff_region', :locals => {:keyboards_for_language_iso => @keyboards_for_language_iso,:all_regions => @all_regions}
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
end

