class ApplicationController < ActionController::Base
  # Prevent CSRF attacks by raising an exception.
  # For APIs, you may want to use :null_session instead.
  protect_from_forgery with: :exception
  #autocomplete :KeyboardLanguages, :lr

  helper_method :get_all_languages
  def get_all_languages
    Keyboard.all
  end

  helper_method :get_all_languages_distinct
  def get_all_languages_distinct
    Keyboard.select(:iso_language).order(:iso_language).uniq
  end
  helper_method :get_regions
  def get_regions
    sql = 'SELECT kc.cc,kl.lr FROM keyboard_countries kc, keyboard_languages kl, (SELECT DISTINCT ON("keyboardCountry_id") "keyboardCountry_id", "keyboardLanguages_id" FROM lang_regions ORDER BY "keyboardCountry_id") lg WHERE kc.id=lg."keyboardCountry_id" AND kl.id=lg."keyboardLanguages_id";'
    c = ActiveRecord::Base.connection.execute(sql)
    c
  end
  helper_method :get_languages
  def get_languages
	   KeyboardLanguages.all.limit(200).offset(0)
  end
  helper_method :languages_count
  def languages_count
    get_all_languages_distinct.count
  end

  helper_method :languages_per_column
  def languages_per_column
    get_all_languages_distinct.count / 3
  end

  helper_method :new_keyboard
  def new_keyboard
    Keyboard.new
  end

  helper_method :get_all_regions_distinct
  def get_all_regions_distinct
    Keyboard.select(:iso_region).order(:iso_region).uniq
  end

  helper_method :regions_count
  def regions_count
    get_all_regions_distinct.count
  end

  helper_method :regions_per_column
  def regions_per_column
    get_all_regions_distinct.count / 3
  end

  helper_method :get_regions_with_code_name
  def get_regions_with_code_name
    @region_array = [];
    get_all_regions_distinct.each do |region|
      if !region.iso_region.nil?
        data = region.region_name
        unless @region_array.include?(data)
          @region_array << data
        end
      end
    end
    @region_with_code = []

    @region_array.each do |region_name|
      @region_code = []
      get_all_regions_distinct.each do |region|
        if !region.iso_region.nil?
          if region_name == region.region_name
            unless @region_code.include?(region.iso_region)
              @region_code << region.iso_region
            end
          end
        end
      end
      data = {:region_name => region_name, :region_code => @region_code}
      @region_with_code << data
    end
    @region_with_code
  end

  helper_method :regions_name_count
  def regions_name_count
    get_all_regions_distinct.count
  end

  helper_method :regions_name_per_column
  def regions_name_per_column
    get_all_regions_distinct.count / 3
  end

end

