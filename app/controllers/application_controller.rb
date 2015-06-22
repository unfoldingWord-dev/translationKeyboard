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

  helper_method :languages_count
  def regions_count
    get_all_regions_distinct.count
  end

  helper_method :languages_per_column
  def regions_per_column
    get_all_regions_distinct.count / 3
  end

end

