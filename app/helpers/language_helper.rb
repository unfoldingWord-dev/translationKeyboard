module LanguageHelper

  def get_keyboards_for_language (language_id)
    @keyboards_for_language_iso = Keyboard.where(iso_language: language_id)
    @language_details =  KeyboardLanguages.find_language(language_id)
    sql = 'SELECT kc.cc,kl.lr FROM keyboard_countries kc, keyboard_languages kl, (SELECT DISTINCT ON("keyboardCountry_id") "keyboardCountry_id", "keyboardLanguages_id" FROM lang_regions ORDER BY "keyboardCountry_id") lg WHERE kc.id=lg."keyboardCountry_id" AND kl.id=lg."keyboardLanguages_id";'
    @all_regions = ActiveRecord::Base.connection.execute(sql)
  end

  def get_keyboards_for_region(region_id)
    @keyboards_for_region_iso = Keyboard.where(iso_region: region_id)
    arr = Array.new
    arr[0] = region_id
    country = KeyboardCountry.where(:cc => arr).first
    country_lang = LangRegions.where(:keyboardCountry_id => country).first
    lang_id = country_lang.keyboardLanguages_id
    @region_name = KeyboardLanguages.find(lang_id).lr

    sql = 'SELECT kc.cc,kl.lr FROM keyboard_countries kc, keyboard_languages kl, (SELECT DISTINCT ON("keyboardCountry_id") "keyboardCountry_id", "keyboardLanguages_id" FROM lang_regions ORDER BY "keyboardCountry_id") lg WHERE kc.id=lg."keyboardCountry_id" AND kl.id=lg."keyboardLanguages_id";'
    @all_regions = ActiveRecord::Base.connection.execute(sql)
  end

end
