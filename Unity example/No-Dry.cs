using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using TMPro;
public class ResourcesUI : MonoBehaviour
{
    private ResourceTypeListSO resourceTypeList;
    private Dictionary<ResourceTypeSO,Transform> resourceTypeTransform;
    private void Awake()
    {
        resourceTypeList = (Resources.Load<ResourceTypeListSO>(typeof(ResourceTypeListSO).Name));
        resourceTypeTransform = new Dictionary<ResourceTypeSO, Transform>();
        Transform resourceTemplate = transform.Find("resourceTemplate");
        resourceTemplate.gameObject.SetActive(false);
        
        Transform wood = Instantiate(resourceTemplate,transform);
        wood.gameObject.SetActive(true);
        wood.GetComponent<RectTransform>().anchoredPosition = new Vector2(-160,0);
        wood.Find("image").GetComponent<Image>().sprite = resourceTypeList.list[0].sprite;
        resourceTypeTransform[resourceTypeList.list[0]] = wood;
        
        Transform stone = Instantiate(resourceTemplate,transform);
        stone.gameObject.SetActive(true);
        stone.GetComponent<RectTransform>().anchoredPosition = new Vector2(-300,0);
        wood.Find("image").GetComponent<Image>().sprite = resourceTypeList.list[1].sprite;
        resourceTypeTransform[resourceTypeList.list[1]] = stone;
    }
    private void Start()
    {
        ResourceManager.Instance.OnResourceAmountChanged += ResourceManager_OnResourceAmountChanged;
        UpdateResourceAmount();
    }

    private void ResourceManager_OnResourceAmountChanged(object sender, System.EventArgs e)
    {
        UpdateResourceAmount();
    }

    private void UpdateResourceAmount()
    {
        foreach(ResourceTypeSO resourceType in resourceTypeList.list)
        {
            Transform resourceTransform = resourceTypeTransform[resourceType];

            int resourceAmount = ResourceManager.Instance.GetResourceAmount(resourceType);
            resourceTransform.Find("text").GetComponent<TextMeshProUGUI>().SetText(resourceAmount.ToString());
        }
    }
}
