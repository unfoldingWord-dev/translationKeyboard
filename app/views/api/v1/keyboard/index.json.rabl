# app/views/api/v1/keyboard/index.json.rabl

object false

child @keyboards, :object_root => false do
  attributes :id, :iso_language, :iso_region, :language_name
  attributes :updated_at_epoch => :updated_at
end
