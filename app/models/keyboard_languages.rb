class KeyboardLanguages < ActiveRecord::Base
	has_many :lang_regions
  	has_many :keyboard_countries, through: :lang_regions

  def funky_method
    "#{self.lc}-#{self.ln}"
  end

  def self.find_language(lc)
    self.where(:lc => lc).first
  end
end
