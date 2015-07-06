# == Schema Information
#
# Table name: keyboards
#
#  id         :integer          not null, primary key
#  name       :string(255)
#  created_at :datetime
#  updated_at :datetime
#

class KeyboardType < ActiveRecord::Base
  has_many :keyboard_variants
  has_many :keyboard_type_default_key_positions

  def icon_path
    'icons/' + os.downcase + '.png'
  end

  def icon_class
    'icon-' + os.downcase
  end

end
