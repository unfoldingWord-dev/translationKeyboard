# == Schema Information
#
# Table name: keyboards
#
#  id         :integer          not null, primary key
#  name       :string(255)
#  created_at :datetime
#  updated_at :datetime
#

class Keyboard < ActiveRecord::Base
	has_many :keyboard_variants

  def language_name
   # language_iso_obj.name
     language_iso_obj.ln
  end

  def language_iso_639_1
    if language_iso_obj.nil?
      iso_language
    else
      #language_iso_obj.iso_639_1
      language_iso_obj.lc
    end
  end

  # @return [string]
  def language_name_and_iso_639_1
    if language_iso_obj.nil?
      iso_language
    else
      language_name.to_s + ' (' + language_iso_639_1 + ')'
    end
  end

  def language_iso_639_3
    language_iso_obj.iso_639_3
  end

  def language_iso_obj
    #LanguageList::LanguageInfo.find(iso_language)
    lang = KeyboardLanguages.where(:lc => iso_language)
    lang_details = KeyboardLanguages.find(lang)
  end

  def region_name
    #region_obj.name
    region_obj.lr
  end

  def created_at_epoch
    created_at.to_f
  end

  def updated_at_epoch
    updated_at.to_f
  end

  def region_obj
    if iso_region == '00'
      c = KeyboardLanguages.new({:lr => "Unknown Region"})
    else
      #c = Country.find_country_by_alpha2(iso_region)
      arr = Array.new
      arr[0] = iso_region
      country = KeyboardCountry.where(:cc => arr).first
      country_lang = LangRegions.where(:keyboardCountry_id => country)
      country_lang.each do |p|
        lang_id = p.keyboardLanguages_id
        c = KeyboardLanguages.find(lang_id)
      end
      if c.nil?
        country = KeyboardCountry.new({:cc => arr})
        c = KeyboardLanguages.new({:lr => "Unknown Name"})
      end
    end
    c
	#country
  end
end
