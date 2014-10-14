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
    language_iso_obj.name
  end

  def language_iso_639_1
    if language_iso_obj.nil?
      iso_language
    else
      language_iso_obj.iso_639_1
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
    LanguageList::LanguageInfo.find(iso_language)
  end

  def region_name
    region_obj.name
  end



  def region_obj
    if iso_region == '00'
      c = Country.new({alpha2: '00', name:"Default Region"})
    else
      c = Country.find_country_by_alpha2(iso_region)
      if c.nil?
        c = Country.new({alpha2: iso_region, name:"Unknown Name"})
      end
    end
    c
  end
end
