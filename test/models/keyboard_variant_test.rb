# == Schema Information
#
# Table name: keyboard_variants
#
#  id          :integer          not null, primary key
#  name        :string(255)
#  type        :integer
#  created_at  :datetime
#  updated_at  :datetime
#  keyboard_id :integer
#

require 'test_helper'

class KeyboardVariantTest < ActiveSupport::TestCase
  # test "the truth" do
  #   assert true
  # end
end
